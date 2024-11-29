package dev.kostromdan.mods.crash_assistant.utils;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatExecutor {
    public static final Path HEARTBEAT_FILE = Paths.get("local", "crash_assistant", "heartbeat.tmp");
    private static ScheduledExecutorService heartbeatExecutor = null;


    public static void startHeartbeat() throws IOException {
        Files.createDirectories(HEARTBEAT_FILE.getParent());

        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                Files.write(HEARTBEAT_FILE, Long.toString(System.currentTimeMillis()).getBytes());
            } catch (IOException e) {
                CrashAssistant.LOGGER.error("Failed to update heartbeat file", e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(HeartbeatExecutor::stopHeartbeat));
    }

    public static void stopHeartbeat() {
        try {
            if (heartbeatExecutor != null) {
                heartbeatExecutor.shutdownNow();
            }
            Files.deleteIfExists(HEARTBEAT_FILE);
        } catch (IOException ignored) {
        }

    }
}
