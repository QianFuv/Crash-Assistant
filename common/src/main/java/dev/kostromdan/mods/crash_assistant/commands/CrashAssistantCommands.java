package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.lang.LanguageProvider;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListDiff;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class CrashAssistantCommands {
    public static Component getModConfigComponent() {
        return Component.literal("[mod config]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, CrashAssistantConfig.getConfigPath().toAbsolutePath().toString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(LanguageProvider.get("commands.mod_config_tooltip"))))
                );
    }

    public static Component getCopyUUIDComponent(String playerUUID) {
        return Component.literal("[UUID]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, '"' + playerUUID + '"'))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(LanguageProvider.get("commands.uuid_tooltip"))))
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
                )
        );
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
        Minecraft.getInstance().player.sendSystemMessage(msg);
        return false;
    }

    public static int saveModlist(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (!checkModlistFeatureEnabled()) {
            return 0;
        }

        MutableComponent msg = Component.empty();

        if (CrashAssistantConfig.getModpackCreators().contains(CrashAssistant.playerUUID)) {
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
            msg.append(getCopyUUIDComponent(CrashAssistant.playerUUID));
            msg.append(Component.literal(LanguageProvider.get("commands.add_to_creator_list_msg")));
            msg.append(getModConfigComponent());
            msg.withStyle(ChatFormatting.RED);
        }

        player.sendSystemMessage(msg);
        return 0;
    }

    public static int showDiff(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
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

        player.sendSystemMessage(msg);
        return 0;
    }
}