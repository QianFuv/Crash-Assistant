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
        addOption("general.help_link",
                "Link which will be opened in browser on request_help_button pressed.",
                "https://discord.gg/moddedmc");

        config.setComment("debug", "Here you can configure debug options for easier configuration of the mod.");
        addOption("debug.crash_game_on_event",
                "Setting this value to one of listed here, will crash the game in order to show/debug gui.\n" +
                        "NONE - default value, no crash. You can always crash game by holding vanilla F3+C keybind.\n" +
                        "MIXIN_SETUP - will crash game on Mixin setup. Crash report not generated.\n" +
                        "GAME_STARTED - will crash game on first tick of TitleScreen. Crash report generated.",
                "NONE");

        config.setComment("text", "Here you can change text of buttons, generated msg, etc");
        addOption("text.request_help_button",
                "Text of request_help_button",
                "request help in Modded Minecraft Discord");
        addOption("text.msg",
                "Initial text of generated msg with links to all files.",
                "Minecraft crashed!");
        addOption("text.title_crashed_with_report",
                "Title label text, then crash report or hs_err exists.",
                "Oops, Minecraft crashed!");
        addOption("text.title_crashed_without_report",
                "Title label text, then no crash report exists.",
                "Oops, Minecraft crashed without crash report!");
    }

    private static void addOption(String path, String comment, String defaultValue) {
        config.setComment(path, comment);
        if (!config.contains(path)) {
            config.set(path, defaultValue);
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
