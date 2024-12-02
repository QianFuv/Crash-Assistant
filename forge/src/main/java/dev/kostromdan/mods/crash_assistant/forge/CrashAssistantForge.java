package dev.kostromdan.mods.crash_assistant.forge;

import net.minecraftforge.fml.common.Mod;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;

@Mod(CrashAssistant.MOD_ID)
public final class CrashAssistantForge {
    public CrashAssistantForge() {
        // Run our common setup.
        CrashAssistant.init();
    }
}
