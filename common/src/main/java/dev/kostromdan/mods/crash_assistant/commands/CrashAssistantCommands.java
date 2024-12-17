package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListDiff;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class CrashAssistantCommands {
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

    public static <S> int saveModlist(CommandContext<S> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        String UUID = Minecraft.getInstance().getUser().getUuid();

        Component modConfig = Component.literal("[mod config]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, CrashAssistantConfig.getConfigPath().toAbsolutePath().toString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open config")))
                );
        Component copyUUID = Component.literal("[UUID]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.YELLOW)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, '"' + UUID + '"'))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy UUID")))
                );
        MutableComponent msg = Component.empty();

        if (CrashAssistantConfig.getModpackCreators().contains(UUID)) {
            ModListUtils.saveCurrentModList();
            msg.append(Component.literal("Modpack modlist overwritten successfully!\n"));
            if (CrashAssistantConfig.get("modpack_modlist.auto_update")) {
                msg.append(Component.literal("You actually don't need to perform this command, " +
                                "since auto update feature is enabled in ")
                        .withStyle(style -> style.withColor(ChatFormatting.WHITE)));
            } else {
                msg.append(Component.literal("You can always enable automatic modlist overwriting in\n")
                        .withStyle(style -> style.withColor(ChatFormatting.WHITE)));
            }
            msg.append(modConfig);
            msg.withStyle(ChatFormatting.GREEN);
        } else {
            msg.append(Component.literal("You are not creator of this modpack!\n"
                    + "Overwriting modlist by the end user will create problems to modpack authors with helping you!\n"
                    + "If you think what it's a mistake, add your "));
            msg.append(copyUUID);
            msg.append(Component.literal(" to modpack creators list in "));
            msg.append(modConfig);
            msg.withStyle(ChatFormatting.RED);
        }
        player.sendSystemMessage(msg);
        return 0;
    }

    public static <S> int showDiff(CommandContext<S> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        MutableComponent msg = Component.empty();
        ModListDiff diff = ModListUtils.getDiff();

        msg.append(Component.literal("Added mods: \n").withStyle(style -> style.withColor(ChatFormatting.DARK_GREEN)));
        if (!diff.addedMods().isEmpty()) {
            msg.append(Component.literal(String.join("\n", diff.addedMods())).withStyle(style -> style.withColor(ChatFormatting.GREEN)));
        } else {
            msg.append(Component.literal("Added mods not found!").withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        }
        msg.append(Component.literal("\n"));

        msg.append(Component.literal("Removed mods: \n").withStyle(style -> style.withColor(ChatFormatting.DARK_RED)));
        if (!diff.removedMods().isEmpty()) {
            msg.append(Component.literal(String.join("\n", diff.removedMods())).withStyle(style -> style.withColor(ChatFormatting.RED)));
        } else {
            msg.append(Component.literal("Removed mods not found!").withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
        }

        player.sendSystemMessage(msg);
        return 0;
    }
}
