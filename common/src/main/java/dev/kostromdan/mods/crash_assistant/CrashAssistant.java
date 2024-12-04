package dev.kostromdan.mods.crash_assistant;

import com.mojang.logging.LogUtils;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import org.slf4j.Logger;

public final class CrashAssistant {
    public static final String MOD_ID = "crash_assistant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
//        ManualCrashThrower.crashGame(); // debug crash.
    }
}
