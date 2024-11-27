package dev.kostromdan.mods.crash_assistant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CrashAssistant.MODID)
public class CrashAssistant
{
    public static final String MODID = "crash_assistant";
    public static final Logger LOGGER = LogUtils.getLogger();
}
