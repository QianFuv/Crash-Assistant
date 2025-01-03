package dev.kostromdan.mods.crash_assistant.fabric.client;

import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public final class CrashAssistantFabricClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("unchecked")
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(CrashAssistantCommands.getCommands());
        });
    }
}