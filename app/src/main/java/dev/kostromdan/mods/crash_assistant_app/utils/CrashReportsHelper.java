package dev.kostromdan.mods.crash_assistant_app.utils;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class CrashReportsHelper {
    private static final HashSet<Path> knownCrashReports = new HashSet<>();

    /**
     * Caches the current set of crash reports to identify new reports later.
     * This method updates the known crash reports list with all crash reports
     * available in the directory at the time of invocation.
     */
    public static void cacheKnownCrashReports() {
        knownCrashReports.addAll(getCrashReports());
    }

    /**
     * Scans the crash reports directory for any new reports that were not
     * previously cached. New crash reports are identified as files present
     * in the directory but missing from the known crash reports set.
     *
     * @return A set of paths representing the newly discovered crash reports.
     */
    public static HashSet<Path> scanForNewCrashReports() {
        HashSet<Path> newCrashReports = getCrashReports();
        newCrashReports.removeAll(knownCrashReports);
        return newCrashReports;
    }

    /**
     * Retrieves all crash reports currently present in the crash reports directory.
     * This method scans the directory for files and returns their paths in a set.
     *
     * @return A set of paths representing all crash reports in the directory.
     * If the directory does not exist or an error occurs, an empty set is returned.
     */
    public static HashSet<Path> getCrashReports() {
        HashSet<Path> locatedCrashReports = new HashSet<>();

        Path dir = Paths.get("crash-reports");

        if (!Files.exists(dir)) {
            return locatedCrashReports;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    locatedCrashReports.add(entry);
                }
            }
        } catch (IOException e) {
            CrashAssistantApp.LOGGER.error("Error while scanning crash reports: ", e);
            return new HashSet<>();
        }
        return locatedCrashReports;
    }
}
