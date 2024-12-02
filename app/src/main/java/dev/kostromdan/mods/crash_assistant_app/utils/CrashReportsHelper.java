package dev.kostromdan.mods.crash_assistant_app.utils;

import dev.kostromdan.mods.crash_assistant_app.App;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class CrashReportsHelper {
    private static final Set<String> knownCrashReports = new HashSet<>();

    public static void cacheKnownCrashReports() {
        Path dir = Paths.get("crash-reports");

        if (!Files.exists(dir)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    knownCrashReports.add(entry.getFileName().toString());
                }
            }
        } catch (IOException e) {
            App.LOGGER.error("Error while scanning crash reports: ", e);
        }
    }

    public static Path scanForNewCrashReports() {
        Path dir = Paths.get("crash-reports");

        if (!Files.exists(dir)) {
            return null;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    if (!knownCrashReports.contains(entry.getFileName().toString())) {
                        return entry;
                    }
                }
            }
        } catch (IOException e) {
            App.LOGGER.error("Error while scanning crash reports: ", e);
        }
        return null;

    }
}
