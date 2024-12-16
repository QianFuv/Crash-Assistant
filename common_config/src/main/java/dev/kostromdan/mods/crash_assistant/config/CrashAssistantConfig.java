package dev.kostromdan.mods.crash_assistant.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CrashAssistantConfig {
    private static final Path CONFIG_PATH = Paths.get("config", "crash_assistant", "config.toml");
    private static final Path CONFIG_LOCK_PATH = Paths.get("local", "crash_assistant", "CONFIG_LOCK.tmp");
    private static final Logger LOGGER = LogManager.getLogger();

    private static CommentedFileConfig config;
    private static long lastConfigUpdate;

    static {
        executeWithLock(() -> {
            config = CommentedFileConfig.builder(CONFIG_PATH)
                    .preserveInsertionOrder()
                    .build();
            load();
        });
    }

    private static void setupDefaultValues() {
        config.setComment("general", "General settings of Crash Assistant mod.");
        addOption("general.help_link",
                "Link which will be opened in browser on request_help_button pressed.",
                "https://discord.gg/moddedmc");
        addOption("general.show_on_fml_error_screen",
                "Show gui on minecraft crashed on modloading and FML error screen displayed.",
                true);

        config.setComment("debug", "Here you can configure debug options for easier configuration of the mod.");
        addOption("debug.crash_game_on_event",
                "Setting this value to one of listed here, will crash the game in order to show/debug gui.\n" +
                        "NONE - default value, no crash. You can always crash game by holding vanilla F3+C keybind.\n" +
                        "MIXIN_SETUP - will crash game on Mixin setup. Crash report not generated.\n" +
                        "MOD_LOADING - will crash game on load of this mod. Can be used to show FML error screen. Crash report generated.\n" +
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

        config.setComment("modpack_modlist", "Settings of modlist feature.\n" +
                "Adds in generated msg block about which mods modpack user added/removed/updated.");
        addOption("modpack_modlist.enabled",
                "Enable feature.",
                true);
        addOption("modpack_modlist.modpack_creators",
                "UUID's of players, who considered as modpack creator.\n" +
                        "Only this players can overwrite modlist.json",
                new ArrayList<String>());
        addOption("modpack_modlist.auto_update",
                "If enabled, modlist.json will be overwritten on every launch(mod loading),\n" +
                        "then game is launched by modpack creator.\n" +
                        "So you won't forget to save it before publishing.",
                true);
    }

    private static <T> void addOption(String path, String comment, T defaultValue) {
        config.setComment(path, comment);
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        } else if (config.get(path).getClass() != defaultValue.getClass()) {
            LOGGER.warn("Error while reading config param: '" + path + "'. Current value class:'" + config.get(path).getClass().getName() + "' is not equal to needed:'" + defaultValue.getClass().getName() + "'. Resetting to default!");
            config.set(path, defaultValue);
        }
    }

    public static ArrayList<String> getModpackCreators() {
        return get("modpack_modlist.modpack_creators");
    }

    public static void addModpackCreator(String UUID) {
        ArrayList<String> currentModpackCreators = getModpackCreators();
        currentModpackCreators.add(UUID);
        set("modpack_modlist.modpack_creators", currentModpackCreators);
    }

    public static Path getConfigPath() {
        return CONFIG_PATH;
    }


    private static void executeWithLock(Runnable function) {
        CONFIG_PATH.getParent().toFile().mkdirs();
        CONFIG_LOCK_PATH.toFile().getParentFile().mkdirs();
        try (var lockChannel = FileChannel.open(CONFIG_LOCK_PATH, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var lock = lockChannel.lock()) {
            function.run();
        } catch (OverlappingFileLockException e) { // Current JVM FileLock already locked, ignoring
            function.run();
        } catch (IOException e) {
            throw new RuntimeException("Error accessing or locking the tmp file.", e);
        }
    }

    public static void update() {
        executeWithLock(() -> {
            if (CONFIG_PATH.toFile().lastModified() > lastConfigUpdate) {
                load();
            }
        });
    }

    public static void load() {
        executeWithLock(() -> {
            try {
                config.load();
            } catch (ParsingException e) {
                LOGGER.error("Error while loading config, saved old problematic config as 'config.toml.bak', resetting 'config.toml' to default values:", e);
                try {
                    CONFIG_PATH.toFile().renameTo(Paths.get(CONFIG_PATH.getParent().toString(), "config.toml.bak").toFile());
                } catch (Exception e1) {
                    LOGGER.error("Failed to rename 'config.toml' to 'config.toml.bak': ", e1);
                }
                config.clear();
            }
            int old_values_hash = config.valueMap().hashCode();
            int old_comments_hash = config.commentMap().hashCode();
            setupDefaultValues();
            if (config.valueMap().hashCode() != old_values_hash || config.commentMap().hashCode() != old_comments_hash) {
                save();
            }
        });
    }

    public static void save() {
        executeWithLock(() -> {
            config.save();
            lastConfigUpdate = CONFIG_PATH.toFile().lastModified();
        });
    }

    public static <T> T get(String path) {
        final AtomicReference<T> result = new AtomicReference<>();
        executeWithLock(() -> {
            update();
            result.set(config.get(path));
        });
        return result.get();
    }

    public static <T> void set(String path, T value) {
        executeWithLock(() -> {
            update();
            config.set(path, value);
            save();
        });
    }

    public static void main(String[] args) { // Debug config.
    }
}
