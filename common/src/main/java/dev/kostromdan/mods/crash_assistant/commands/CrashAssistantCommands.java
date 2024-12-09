package dev.kostromdan.mods.crash_assistant.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import net.minecraft.commands.CommandBuildContext;

public class CrashAssistantCommands {
    public static void register(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommandRegistrationEvent.literal("crash_assistant")
                .then(ClientCommandRegistrationEvent.literal("save_modlist")
                        .executes(context -> {
                                    CrashAssistant.LOGGER.info("Save modlist");
                                    return 0;
                                }
                        )
                )
        );
    }
}
