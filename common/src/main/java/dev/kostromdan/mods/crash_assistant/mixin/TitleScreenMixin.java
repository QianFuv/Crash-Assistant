package dev.kostromdan.mods.crash_assistant.mixin;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    static {
        if (Objects.equals(CrashAssistantConfig.get("debug.crash_game_on_event").toString(), "MIXIN_SETUP")) {
            ManualCrashThrower.crashGame("Debug crash from Crash Assistant mod. 'debug.crash_game_on_event' value of '" + CrashAssistantConfig.getConfigPath() + "' set to 'MIXIN_SETUP'.");
        }
    }

    @Inject(method = "tick", at = @At("RETURN"), cancellable = false)
    private void onClientLoaded(CallbackInfo ci) {
        if (CrashAssistant.clientLoaded) {
            return;
        }
        CrashAssistant.clientLoaded = true;
        if (Objects.equals(CrashAssistantConfig.get("debug.crash_game_on_event").toString(), "GAME_STARTED")) {
            ManualCrashThrower.crashGame("Debug crash from Crash Assistant mod. 'debug.crash_game_on_event' value of '" + CrashAssistantConfig.getConfigPath() + "' set to 'GAME_STARTED'.");
        }
    }
}
