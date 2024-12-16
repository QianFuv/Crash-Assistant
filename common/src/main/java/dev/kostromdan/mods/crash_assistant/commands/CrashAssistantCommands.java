package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class CrashAssistantCommands {
    public static void register(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommandRegistrationEvent.literal("crash_assistant")
                .then(ClientCommandRegistrationEvent.literal("modlist")
                        .then(ClientCommandRegistrationEvent.literal("save")
                                .executes(context -> {
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
                                                ModListUtils.save();
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
                                )
                        )
                )
        );
    }
}
