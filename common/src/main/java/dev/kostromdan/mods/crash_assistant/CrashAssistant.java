package dev.kostromdan.mods.crash_assistant;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;
import dev.kostromdan.mods.crash_assistant.utils.ManualCrashThrower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.Objects;

public final class CrashAssistant {
    public static final String MOD_ID = "crash_assistant";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean clientLoaded = false;


    public static void init() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> CrashAssistant.Client::initializeClient);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Environment(EnvType.CLIENT)
        public static void initializeClient() {
            ClientCommandRegistrationEvent.EVENT.register(CrashAssistantCommands::register);

            if (CrashAssistantConfig.get("modpack_modlist.enabled")) {
                String userUUID = Minecraft.getInstance().getUser().getUuid();
                if (CrashAssistantConfig.getModpackCreators().isEmpty()) {
                    CrashAssistantConfig.addModpackCreator(userUUID);
                }
                if ((Boolean) CrashAssistantConfig.get("modpack_modlist.auto_update") &&
                        CrashAssistantConfig.getModpackCreators().contains(userUUID)) {
                    ModListUtils.saveCurrentModList();
                }
            }
            if (Objects.equals(CrashAssistantConfig.get("debug.crash_game_on_event").toString(), "MOD_LOADING")) {
                ManualCrashThrower.crashGame("Debug crash from Crash Assistant mod. 'debug.crash_game_on_event' value of '" + CrashAssistantConfig.getConfigPath() + "' set to 'MOD_LOADING'.");
            }
        }
    }
}
