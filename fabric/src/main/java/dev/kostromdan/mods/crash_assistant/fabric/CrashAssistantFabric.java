package dev.kostromdan.mods.crash_assistant.fabric;

import net.fabricmc.api.ModInitializer;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;

public final class CrashAssistantFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CrashAssistant.init();
    }
}
