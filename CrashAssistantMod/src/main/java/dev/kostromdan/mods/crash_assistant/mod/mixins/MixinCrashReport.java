package dev.kostromdan.mods.crash_assistant.mod.mixins;

import com.mojang.logging.LogUtils;
import dev.kostromdan.mods.crash_assistant.mod.CrashAssistant;
import net.minecraft.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Mixin(CrashReport.class)
public class MixinCrashReport {
    static {
        // Start CrashAssistantApp on mixin setup. further: do this in coremod, to handle some early exceptions on mixin setup.
//        JarExtractor.launchCrashAssistantApp();
        LogUtils.getLogger().info("MixinCrashReport CrashAssistant");
    }

    @Inject(method = "saveToFile", at = @At("RETURN"))
    private void afterSaveToFile(File p_127513_, CallbackInfoReturnable<Boolean> cir) {
        CrashAssistant.LOGGER.info("saveToFile called");
        try {
            Files.write(Paths.get("local", "crash_assistant", "crashed.tmp"), Long.toString(System.currentTimeMillis()).getBytes());
        } catch (IOException ignored) {
        }
    }
}
