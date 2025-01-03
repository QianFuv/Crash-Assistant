package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.lang.LanguageProvider;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListDiff;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import dev.kostromdan.mods.crash_assistant.utils.HeapDumper;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import dev.kostromdan.mods.crash_assistant.utils.ThreadDumper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CrashAssistantCommands {
    public static Instant lastCrashCommand = Instant.ofEpochMilli(0);
    public static final HashMap<String, String> supportedCrashCommands = new HashMap<>() {{
        put("game", "Minecraft");
        put("jwm", "JVM");
        put("no_crash", "noCrash");
    }};
    public static final HashSet<String> supportedCrashArgs = new HashSet<>() {{
        add("--withThreadDump");
        add("--withHeapDump");
        add("--GCBeforeHeapDump");
    }};

    @SuppressWarnings("unchecked")
    public static <S> LiteralArgumentBuilder<S> getCommands() {
        return LiteralArgumentBuilder.literal("crash_assistant")
                .then(LiteralArgumentBuilder.literal("modlist")
                        .then(LiteralArgumentBuilder.literal("save")
                                .executes(CrashAssistantCommands::saveModlist)
                        ).then(LiteralArgumentBuilder.literal("diff")
                                .executes(CrashAssistantCommands::showDiff)
                        )
                ).then(LiteralArgumentBuilder.literal("crash")
                        .requires(c -> CrashAssistantConfig.get("crash_command.enabled"))
                        .then(RequiredArgumentBuilder.argument("to_crash", StringArgumentType.string())
                                .suggests(new CrashAssistantCommands.CrashCommandsSuggestionProvider<>())
                                .executes(CrashAssistantCommands::crash)
                                .then(getCrashArg(1)
                                        .then(getCrashArg(2)
                                                .then(getCrashArg(3))))));
    }

    public static Component getModConfigComponent() {
        return Component.literal("[mod config]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, CrashAssistantConfig.getConfigPath().toAbsolutePath().toString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(LanguageProvider.get("commands.mod_config_tooltip"))))
                );
    }

    public static Component getCopyNicknameComponent(String playerNickname) {
        return Component.literal("[nickname]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, '"' + playerNickname + '"'))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(LanguageProvider.get("commands.nickname_tooltip"))))
                );
    }

    public static void sendClientMsg(Component message) {
        Minecraft.getInstance().gui.getChat().addMessage(message);
    }

    public static boolean checkModlistFeatureEnabled() {
        LanguageProvider.updateLang();
        if (CrashAssistantConfig.get("modpack_modlist.enabled")) {
            return true;
        }
        MutableComponent msg = Component.empty();
        msg.append(Component.literal(LanguageProvider.get("commands.modlist_disabled_error_msg")));
        msg.append(getModConfigComponent());
        msg.withStyle(ChatFormatting.RED);

        sendClientMsg(msg);
        return false;
    }

    public static int saveModlist(CommandContext<?> context) {
        if (!checkModlistFeatureEnabled()) {
            return 0;
        }

        MutableComponent msg = Component.empty();

        if (CrashAssistantConfig.getModpackCreators().contains(CrashAssistant.playerNickname)) {
            ModListUtils.saveCurrentModList();
            msg.append(Component.literal(LanguageProvider.get("commands.modlist_overwritten_success")));
            if (CrashAssistantConfig.get("modpack_modlist.auto_update")) {
                msg.append(Component.literal(LanguageProvider.get("commands.modlist_auto_update_msg"))
                        .withStyle(style -> style.withColor(ChatFormatting.WHITE)));
            } else {
                msg.append(Component.literal(LanguageProvider.get("commands.modlist_enable_auto_update_msg"))
                        .withStyle(style -> style.withColor(ChatFormatting.WHITE)));
            }
            msg.append(getModConfigComponent());
            msg.withStyle(ChatFormatting.GREEN);
        } else {
            msg.append(Component.literal(LanguageProvider.get("commands.not_creator_error_msg")));
            msg.append(getCopyNicknameComponent(CrashAssistant.playerNickname));
            msg.append(Component.literal(LanguageProvider.get("commands.add_to_creator_list_msg")));
            msg.append(getModConfigComponent());
            msg.withStyle(ChatFormatting.RED);
        }

        sendClientMsg(msg);
        return 0;
    }

    public static int showDiff(CommandContext<?> context) {
        MutableComponent msg = Component.empty();

        if (!checkModlistFeatureEnabled()) {
            return 0;
        }

        ModListDiff diff = ModListUtils.getDiff();

        msg.append(Component.literal(LanguageProvider.get("commands.added_mods_header")).withStyle(style -> style.withColor(ChatFormatting.DARK_GREEN)));
        if (!diff.addedMods().isEmpty()) {
            msg.append(Component.literal(String.join("\n", diff.addedMods())).withStyle(style -> style.withColor(ChatFormatting.GREEN)));
        } else {
            msg.append(Component.literal(LanguageProvider.get("commands.added_mods_not_found")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        }
        msg.append(Component.literal("\n"));

        msg.append(Component.literal(LanguageProvider.get("commands.removed_mods_header")).withStyle(style -> style.withColor(ChatFormatting.DARK_RED)));
        if (!diff.removedMods().isEmpty()) {
            msg.append(Component.literal(String.join("\n", diff.removedMods())).withStyle(style -> style.withColor(ChatFormatting.RED)));
        } else {
            msg.append(Component.literal(LanguageProvider.get("commands.removed_mods_not_found")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        }

        sendClientMsg(msg);
        return 0;
    }

    public static int crash(CommandContext<?> context) {
        LanguageProvider.updateLang();
        MutableComponent msg = Component.empty();
        String toCrash = "null";
        try {
            toCrash = context.getArgument("to_crash", String.class);
            if (!supportedCrashCommands.containsKey(toCrash)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ignored) {
            sendClientMsg(Component.literal(LanguageProvider.get("commands.crash_command_validation_failed_to_crash") + " '" + toCrash + "'").withStyle(style -> style.withColor(ChatFormatting.RED)));
            return 0;
        }
        toCrash = supportedCrashCommands.get(toCrash);

        int secondsToCrash = CrashAssistantConfig.get("crash_command.seconds");
        boolean noCrash = Objects.equals(toCrash, "noCrash");
        if (secondsToCrash <= 0 || Instant.now().isBefore(lastCrashCommand.plusSeconds(secondsToCrash)) || noCrash) {
            List<String> args = parseCrashArgs(context);
            if (!validateCrashArgs(args)) return 0;
            if (!args.isEmpty())
                sendClientMsg(Component.literal(LanguageProvider.get("commands.crash_command_applying_args")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
            if (args.contains("--withThreadDump")) {
                CrashAssistant.LOGGER.error("Detected '--withThreadDump' crash command argument. ThreadDump:\n" + ThreadDumper.obtainThreadDump());
            }
            if (args.contains("--withHeapDump")) {
                if (args.contains("--GCBeforeHeapDump")) {
                    CrashAssistant.LOGGER.error("Detected '--GCBeforeHeapDump' crash command argument. Performing garbage collection before heap dump.");
                    System.gc();
                }
                CrashAssistant.LOGGER.error("Detected '--withHeapDump' crash command argument. Creating heap dump.");
                try {
                    CrashAssistant.LOGGER.error("Created heap dump at: " + HeapDumper.createHeapDump());
                } catch (Exception e) {
                    CrashAssistant.LOGGER.error("Failed to create heap dump.", e);
                }
            }

            if (!noCrash) {
                sendClientMsg(Component.literal(LanguageProvider.get("commands.crash_command_crashing")).withStyle(style -> style.withColor(ChatFormatting.RED)));
            } else {
                sendClientMsg(Component.literal(LanguageProvider.get("commands.crash_command_done")));
            }

            if (Objects.equals(toCrash, "Minecraft")) {
                ManualCrashThrower.crashGame("Minecraft crashed by '/crash_assistant crash' command.");
            } else if (Objects.equals(toCrash, "JVM")) {
                CrashAssistant.LOGGER.error("JVM crashed by '/crash_assistant crash jvm' command.");
                Blaze3D.youJustLostTheGame();
            }
            return 0;
        }
        lastCrashCommand = Instant.now();

        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_1")));
        msg.append(Component.literal(toCrash).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_2")));
        msg.append(Component.literal(Integer.toString(secondsToCrash)).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_3")));
        msg.withStyle(style -> style.withColor(ChatFormatting.RED));
        sendClientMsg(msg);
        return 0;
    }

    public static boolean validateCrashArgs(List<String> args) {
        for (String arg : args) {
            if (!supportedCrashArgs.contains(arg)) {
                sendClientMsg(Component.literal(LanguageProvider.get("commands.crash_command_validation_failed") + " '" + arg + "'").withStyle(style -> style.withColor(ChatFormatting.RED)));
                return false;
            }
        }
        return true;
    }

    public static List<String> parseCrashArgs(CommandContext<?> context) {
        List<String> args = new ArrayList<>();
        for (int i = 1; i <= supportedCrashArgs.size(); i++) {
            try {
                args.add(context.getArgument("arg" + i, String.class));
            } catch (IllegalArgumentException ignored) {
                break;
            }
        }
        return args;
    }

    public static class CrashArgsSuggestionProvider<S> implements SuggestionProvider<S> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            List<String> existingArgs = parseCrashArgs(context);
            for (String e : supportedCrashArgs) {
                if (existingArgs.contains(e)) continue;
                if (Objects.equals(e, "--GCBeforeHeapDump") && !existingArgs.contains("--withHeapDump")) continue;
                builder.suggest(e);
            }
            return builder.buildFuture();
        }
    }

    public static class CrashCommandsSuggestionProvider<S> implements SuggestionProvider<S> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            for (String crashCommand : supportedCrashCommands.keySet()) {
                builder.suggest(crashCommand);
            }
            return builder.buildFuture();
        }
    }

    public static ArgumentBuilder getCrashArg(int i) {
        return RequiredArgumentBuilder.argument("arg" + i, StringArgumentType.string())
                .suggests(new CrashArgsSuggestionProvider<>())
                .executes(CrashAssistantCommands::crash);
    }
}