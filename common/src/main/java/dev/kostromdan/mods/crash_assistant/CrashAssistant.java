package dev.kostromdan.mods.crash_assistant;

import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public final class CrashAssistant {
    public static final String MOD_ID = "crash_assistant";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean clientLoaded = false;
    public static String playerUUID;

    public static void init() {
        if (CrashAssistantConfig.get("modpack_modlist.enabled")) {
            playerUUID = Minecraft.getInstance().getUser().getUuid();

            if (CrashAssistantConfig.getModpackCreators().isEmpty()) {
                CrashAssistantConfig.addModpackCreator(playerUUID);
            }
            if ((Boolean) CrashAssistantConfig.get("modpack_modlist.auto_update") &&
                    CrashAssistantConfig.getModpackCreators().contains(playerUUID)) {
                ModListUtils.saveCurrentModList();
            }
        }
        if (Objects.equals(CrashAssistantConfig.get("debug.crash_game_on_event").toString(), "MOD_LOADING")) {
            ManualCrashThrower.crashGame("Debug crash from Crash Assistant mod. 'debug.crash_game_on_event' value of '" + CrashAssistantConfig.getConfigPath() + "' set to 'MOD_LOADING'.");
        }
    }
}
