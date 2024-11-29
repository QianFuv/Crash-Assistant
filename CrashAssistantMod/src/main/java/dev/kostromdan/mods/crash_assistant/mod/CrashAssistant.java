package dev.kostromdan.mods.crash_assistant.mod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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
