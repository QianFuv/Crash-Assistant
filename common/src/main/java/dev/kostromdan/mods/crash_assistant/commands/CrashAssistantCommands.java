package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.lang.LanguageProvider;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListDiff;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.time.Instant;
import java.util.Objects;

public class CrashAssistantCommands {
    public static Instant lastCrashCommand = Instant.ofEpochMilli(0);
    public static boolean crashCommandEnabled = CrashAssistantConfig.get("crash_command.enabled");

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

    @SuppressWarnings("unchecked")
    public static <T> void register(CommandDispatcher<T> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder<T>) Commands.literal("crash_assistant")
                .then(Commands.literal("modlist")
                        .then(Commands.literal("save")
                                .executes(CrashAssistantCommands::saveModlist)
                        ).then(Commands.literal("diff")
                                .executes(CrashAssistantCommands::showDiff)
                        )
                ).then(Commands.literal("crash")
                        .then(Commands.literal("game")
                                .executes(CrashAssistantCommands::crashClient)
                        ).then(Commands.literal("jwm")
                                .executes(CrashAssistantCommands::crashJVM)
                        ).executes(CrashAssistantCommands::crashClient)
                )
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

    public static int crashClient(CommandContext<?> context) {
        crash("Minecraft");
        return 0;
    }

    public static int crashJVM(CommandContext<?> context) {
        crash("JVM");
        return 0;
    }

    public static void crash(String toCrash) {
        LanguageProvider.updateLang();
        MutableComponent msg = Component.empty();
        if (!(boolean) CrashAssistantConfig.get("crash_command.enabled")) {
            msg.append(Component.literal(LanguageProvider.get("commands.crash_command_disabled")));
            msg.append(getModConfigComponent());
            msg.withStyle(style -> style.withColor(ChatFormatting.RED));
            sendClientMsg(msg);
            return;
        }

        int secondsToCrash = CrashAssistantConfig.get("crash_command.seconds");
        if (secondsToCrash <= 0 || Instant.now().isBefore(lastCrashCommand.plusSeconds(secondsToCrash))) {
            if (Objects.equals(toCrash, "Minecraft")) {
                ManualCrashThrower.crashGame("Minecraft crashed by '/crash_assistant crash' command.");
            } else if (Objects.equals(toCrash, "JVM")) {
                CrashAssistant.LOGGER.error("JVM crashed by '/crash_assistant crash jvm' command.");
                Blaze3D.youJustLostTheGame();
            }
            return;
        }
        lastCrashCommand = Instant.now();

        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_1")));
        msg.append(Component.literal(toCrash).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_2")));
        msg.append(Component.literal(Integer.toString(secondsToCrash)).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        msg.append(Component.literal(LanguageProvider.get("commands.crash_command_3")));
        msg.withStyle(style -> style.withColor(ChatFormatting.RED));
        sendClientMsg(msg);
    }
}