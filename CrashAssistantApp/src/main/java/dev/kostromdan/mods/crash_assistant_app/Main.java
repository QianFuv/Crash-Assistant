package dev.kostromdan.mods.crash_assistant_app;

import dev.kostromdan.mods.crash_assistant_app.forms.BookEditorExample;
import dev.kostromdan.mods.crash_assistant_app.forms.SaveButtonListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            // Логирование необработанных исключений
            LOGGER.error("Uncaught exception in \"{}\" thread:", thread.getName(), throwable);
        });

        String heartbeatFilePath = null;

        for (int i = 0; i < args.length; i++) {
            if ("-heartbeatFile".equals(args[i]) && i + 1 < args.length) {
                heartbeatFilePath = args[i + 1];
            }
        }

        if (heartbeatFilePath == null) {
            LOGGER.error("No heartbeat file specified.");
            startApp();
            return;
        }

        Path heartbeatFile = Paths.get(heartbeatFilePath);
        long lastCheckTime = System.nanoTime();
        final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(5); // 5 seconds in nanoseconds

        while (true) {
            try {
                // Check if the file exists
                if (!Files.exists(heartbeatFile)) {
                    LOGGER.info("Heartbeat file is missing. Minecraft JVM appears to have stopped.");
                    startApp();
                    return;
                }

                // Read last modified time from heartbeat file
                long lastHeartbeatTime = Long.parseLong(Files.readString(heartbeatFile).trim());
                long currentSystemTime = System.currentTimeMillis();
                long elapsedTime = System.nanoTime() - lastCheckTime;

                // Verify system time hasn't drifted more than the timeout period
                if ((currentSystemTime - lastHeartbeatTime) > 5000L || elapsedTime > TIMEOUT_NANOS) {
                    LOGGER.info("Heartbeat not updated. Minecraft JVM appears to have stopped.");
                    startApp();
                    return;
                }

                // Update last check time
                lastCheckTime = System.nanoTime();

                // Sleep before the next check
                TimeUnit.SECONDS.sleep(1);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void startApp() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BookEditorExample bookEditorExample = new BookEditorExample();
                bookEditorExample.setVisible(true);

                bookEditorExample.setSaveButtonListener(new SaveButtonListener() {
                    @Override
                    public void onSaveClicked(Book book) {
                        LOGGER.info("Entered Book Details:");
                        LOGGER.info("Book Title: " + book.getName());
                        LOGGER.info("Author: " + book.getAuthor().getName());
                        LOGGER.info("Genre: " + book.getGenre());
                        LOGGER.info("Is Unavailable: " + book.isTaken());
                    }
                });
            }
        });
    }
}
