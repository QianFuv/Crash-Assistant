package dev.kostromdan.mods.crash_assistant.mixins;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.utils.JarExtractor;
import net.minecraft.CrashReport;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;


@Mixin(CrashReport.class)
public class MixinCrashReport {
    @Inject(method = "saveToFile", at = @At("RETURN"))
    private void afterSaveToFile(File p_127513_, CallbackInfoReturnable<Boolean> cir) {
        CrashAssistant.LOGGER.info("saveToFile called");
        JarExtractor.launchCrashAssistantApp();
    }
}
