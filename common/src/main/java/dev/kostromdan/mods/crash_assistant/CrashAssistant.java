package dev.kostromdan.mods.crash_assistant;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

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
        }
    }
}
