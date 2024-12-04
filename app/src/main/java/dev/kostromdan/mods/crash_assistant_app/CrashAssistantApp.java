package dev.kostromdan.mods.crash_assistant_app;

import dev.kostromdan.mods.crash_assistant_app.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CrashAssistantApp {
    public static final Logger LOGGER = LogManager.getLogger(CrashAssistantApp.class);
    public static long parentPID;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Uncaught exception in \"{}\" thread:", thread.getName(), throwable);
        });

        parentPID = -1;

        for (int i = 0; i < args.length; i++) {
            if ("-parentPID".equals(args[i]) && i + 1 < args.length) {
                parentPID = Long.parseLong(args[i + 1]);
                LOGGER.info("Parent PID: {}", parentPID);
            }
        }

        FileUtils.removeTmpFiles(Paths.get("local", "crash_assistant"));
        CrashReportsHelper.cacheKnownCrashReports();
        HsErrHelper.removeHsErrLog(parentPID);

        while (true) {
            try {
                if (!PIDHelper.isProcessAlive(parentPID)) {
                    LOGGER.info("PID \"{}\" is not alive. Minecraft JVM appears to have stopped.", parentPID);
                    onMinecraftFinished();
                    return;
                }

                HashSet<Path> newCrashReports = CrashReportsHelper.scanForNewCrashReports();
                if (!newCrashReports.isEmpty()) {
                    LOGGER.info("Detected new crash report(s), awaiting for {} PID finished.", parentPID);
                    ProcessHandle.of(parentPID).get().onExit().get(); // wait until Minecraft jvm finished.
                    LOGGER.info("PID \"{}\" is not alive. Minecraft JVM appears to have stopped.", parentPID);
                    onMinecraftFinished();
                    return;
                }

                TimeUnit.SECONDS.sleep(1);

            } catch (Exception e) {
                LOGGER.error("Exception while awaiting Minecraft stop:", e);
                break;
            }
        }
    }

    private static void onMinecraftFinished() {
        boolean crashed = false;
        boolean crashed_with_report = false;
        SortedMap<String, Path> availableLogs = new TreeMap<>(new LogsComparator());

        FileUtils.addIfExists(availableLogs, Paths.get("logs", "latest.log"));
        FileUtils.addIfExists(availableLogs, Paths.get("logs", "debug.log"));
        FileUtils.addIfExists(availableLogs, "kubejs/client.log", Paths.get("logs", "kubejs", "client.log"));
        FileUtils.addIfExists(availableLogs, "kubejs/server.log", Paths.get("logs", "kubejs", "server.log"));
        FileUtils.addIfExists(availableLogs, "kubejs/startup.log", Paths.get("logs", "kubejs", "startup.log"));

        Optional<Path> hsErrLog = HsErrHelper.locateHsErrLog(parentPID);
        if (hsErrLog.isPresent()) {
            crashed = true;
            crashed_with_report = true;
            availableLogs.put(hsErrLog.get().getFileName().toString(), hsErrLog.get());
        }

        HashSet<Path> newCrashReports = CrashReportsHelper.scanForNewCrashReports();
        if (!newCrashReports.isEmpty()) {
            crashed = true;
            crashed_with_report = true;
            for (Path path : newCrashReports) {
                availableLogs.put(path.getFileName().toString(), path);
            }
        }

        String normalStopFileName = "normal_stop_pid" + parentPID + ".tmp";
        Path normalStopFilePath = Paths.get("local", "crash_assistant", normalStopFileName);
        if (!(Files.exists(normalStopFilePath) || Files.isRegularFile(normalStopFilePath))) {
            crashed = true;
        }

        if (crashed) {
            if (!crashed_with_report) {
                LOGGER.info("Seems like Minecraft crashed without any crash report. Starting Crash Assistant app.");

            } else {
                LOGGER.info("Seems like Minecraft crashed. Starting Crash Assistant app.");
            }
            onMinecraftCrashed(availableLogs);
        } else {
            LOGGER.info("Seems like Minecraft finished normally. Exiting Crash Assistant app.");
        }

    }

    private static void onMinecraftCrashed(Map<String, Path> availableLogs) {
        startApp(availableLogs);
    }


    public static void startApp(Map<String, Path> availableLogs) {
        new CrashAssistantGUI(availableLogs);
    }
}