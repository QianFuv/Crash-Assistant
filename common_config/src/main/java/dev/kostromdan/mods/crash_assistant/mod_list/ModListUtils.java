package dev.kostromdan.mods.crash_assistant.mod_list;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ModListUtils {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Path MODS_FOLDER = Paths.get("mods");
    private static final Path JSON_FILE = Paths.get("config", "crash_assistant", "modlist.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static void save() {
        try {
            Set<String> filenames = new HashSet<>();
            Files.list(MODS_FOLDER).forEach(path -> {
                if (Files.isRegularFile(path)) {
                    filenames.add(path.getFileName().toString());
                }
            });

            try (FileWriter writer = new FileWriter(JSON_FILE.toFile())) {
                GSON.toJson(filenames, writer);
            }

            LOGGER.info("Modlist saved to " + JSON_FILE);
        } catch (IOException e) {
            LOGGER.error("Error while saving Modlist", e);
        }
    }

    public static HashSet<String> get() {
        try {
            if (Files.exists(JSON_FILE)) {
                String json = new String(Files.readAllBytes(JSON_FILE));
                return GSON.fromJson(json, HashSet.class);
            }
        } catch (IOException e) {
            LOGGER.error("Error while getting Modlist", e);
        }
        return new HashSet<>();
    }
}
