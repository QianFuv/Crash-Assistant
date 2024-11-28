package dev.kostromdan.mods.crash_assistant.mixins;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.utils.HeartbeatExecutor;
import dev.kostromdan.mods.crash_assistant.utils.JarExtractor;
import net.minecraft.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;


@Mixin(CrashReport.class)
public class MixinCrashReport {
    static {
        // Start CrashAssistantApp on mixin setup. further: do this in coremod, to handle some early exceptions on mixin setup.
        JarExtractor.launchCrashAssistantApp();
    }

    @Inject(method = "saveToFile", at = @At("RETURN"))
    private void afterSaveToFile(File p_127513_, CallbackInfoReturnable<Boolean> cir) {
        CrashAssistant.LOGGER.info("saveToFile called");
        HeartbeatExecutor.stopHeartbeat();
    }
}
