package dev.kostromdan.mods.crash_assistant;

import com.mojang.logging.LogUtils;
import dev.kostromdan.mods.crash_assistant.utils.JarExtractor;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CrashAssistant.MODID)
public class CrashAssistant
{
    public static final String MODID = "crash_assistant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrashAssistant(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.register(this);

//        ManualCrashThrower.crashGame(); // Test crash
    }

}
