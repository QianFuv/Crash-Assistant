package dev.kostromdan.mods.crash_assistant.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CrashAssistantConfig {
    private static final Path CONFIG_PATH = Paths.get("config/crash_assistant/config.toml");
    private static final CommentedFileConfig config;

    static {
        CONFIG_PATH.getParent().toFile().mkdirs();
        config = CommentedFileConfig.builder(CONFIG_PATH).autosave().autoreload().build();
        config.load();
        setupDefaultValues();
    }

    private static void setupDefaultValues() {
        config.setComment("general", "General settings of Crash Assistant mod.");
        config.setComment("general.help_link", "Link which will be opened in browser on request_help_button pressed.");
        if (!config.contains("general.help_link")) {
            config.set("general.help_link", "https://discord.gg/moddedmc");
        }

        config.setComment("debug", "Here you can configure debug options for easier configuration of the mod.");
        config.setComment("debug.crash_game_on_event", "Setting this value to one of listed here, will crash the game in order to show/debug gui.\n" +
                "NONE - default value, no crash. You can always crash game by holding vanilla F3+C keybind.\n" +
                "MIXIN_SETUP - will crash game on Mixin setup. Crash report not generated.\n" +
                "GAME_STARTED - will crash game on first tick of TitleScreen. Crash report generated.");
        if (!config.contains("debug.crash_game_on_event")) {
            config.set("debug.crash_game_on_event", "NONE");
        }

        config.setComment("text", "Here you can change text of buttons, generated msg, etc");
        config.setComment("text.request_help_button", "Text of request_help_button");
        if (!config.contains("text.request_help_button")) {
            config.set("text.request_help_button", "request help in Modded Minecraft Discord");
        }
        config.setComment("text.msg", "Text before generated msg with links to all files.");
        if (!config.contains("text.msg")) {
            config.set("text.msg", "Minecraft crashed!\n");
        }
    }

    public static <T> T get(String path) {
        return config.get(path);
    }

    public static Path getConfigPath() {
        return CONFIG_PATH;
    }

    public static void onExit() {
        config.close();
    }

    public static void main(String[] args) {
        CrashAssistantConfig c = new CrashAssistantConfig();
        c.onExit();
    }

}
