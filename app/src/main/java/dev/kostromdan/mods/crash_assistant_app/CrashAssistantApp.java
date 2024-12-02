package dev.kostromdan.mods.crash_assistant_app;

import dev.kostromdan.mods.crash_assistant_app.utils.CrashReportsHelper;
import dev.kostromdan.mods.crash_assistant_app.utils.PIDHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CrashAssistantApp {
    public static final Logger LOGGER = LogManager.getLogger(CrashAssistantApp.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Uncaught exception in \"{}\" thread:", thread.getName(), throwable);
        });

        long parentPID = -1;

        for (int i = 0; i < args.length; i++) {
            if ("-parentPID".equals(args[i]) && i + 1 < args.length) {
                parentPID = Long.parseLong(args[i + 1]);
                LOGGER.info("Parent PID: {}", parentPID);
            }
        }

        CrashReportsHelper.cacheKnownCrashReports();

        while (true) {
            try {
                if (!PIDHelper.isProcessAlive(parentPID)) {
                    LOGGER.info("PID \"{}\" is not alive. Minecraft JVM appears to have stopped.", parentPID);
                    onMinecraftFinished();
                    return;
                }

                Path newCrashReport = CrashReportsHelper.scanForNewCrashReports();
                if (newCrashReport != null) {
                    LOGGER.info("New crash-report detected: " + newCrashReport.getFileName().toString());
                    onMinecraftCrashed();
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
        startApp();
    }

    private static void onMinecraftCrashed() {
        startApp();
    }


    public static void startApp() {
        new CrashAssistantGUI();
    }
}