package dev.kostromdan.mods.crash_assistant.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CrashAssistantConfig {
    private static final Path CONFIG_PATH = Paths.get("config/crash_assistant/config.toml");
    private static final CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).autosave().build();

    static {
        CONFIG_PATH.getParent().toFile().mkdirs();
        config.load();
        setupDefaultValues();
    }

    private static void setupDefaultValues() {
        config.setComment("general", "General settings of Crash Assistant mod.");

        config.setComment("general.help_link", "Link which will be opened in browser on request_help_button pressed.");
        if (!config.contains("general.help_link")) {
            config.set("general.help_link", "https://discord.gg/moddedmc");
        }

        config.setComment("text", "Here you can change text of buttons, generated msg, etc");

        config.setComment("text.request_help_button", "Text on request_help_button");
        if (!config.contains("text.request_help_button")) {
            config.set("text.request_help_button", "request help in our Discord");
        }
    }

    public static <T> T get(String path) {
        return config.get(path);
    }

    public static void onExit(){
        config.close();
    }

    public static void main(String[] args) {
        CrashAssistantConfig c = new CrashAssistantConfig();
        c.onExit();
    }

}
