package dev.kostromdan.mods.crash_assistant.utils;

import dev.kostromdan.mods.crash_assistant.CrashAssistant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface JarExtractor {

    static void launchCrashAssistantApp() {
        try {
            HeartbeatExecutor.startHeartbeat();

            Path extractedJarPath = extractCrashAssistantApp();

            ProcessBuilder crashAssistantAppProcess = new ProcessBuilder(
                    "java", "-jar", extractedJarPath.toAbsolutePath().toString(),
                    "-heartbeatFile", '"' + HeartbeatExecutor.HEARTBEAT_FILE.toAbsolutePath().toString() + '"',
                    "-Xmx1024m"
            );
            crashAssistantAppProcess.start();

        } catch (Exception e) {
            CrashAssistant.LOGGER.error("Error while launching GUI: ", e);
        }
    }

    static Path extractCrashAssistantApp() throws IOException {
        String EMBEDDED_JAR_NAME = "CrashAssistantApp.jar";

        Path outputDirectory = Paths.get("local", "crash_assistant");
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        Path extractedJarPath = outputDirectory.resolve(EMBEDDED_JAR_NAME);

        Files.deleteIfExists(extractedJarPath);

        InputStream jarStream = CrashAssistant.class.getResourceAsStream("/" + EMBEDDED_JAR_NAME);
        if (jarStream == null) {
            throw new FileNotFoundException("Could not find embedded JAR: " + EMBEDDED_JAR_NAME);
        }

        try (OutputStream out = Files.newOutputStream(extractedJarPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = jarStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return extractedJarPath;
    }
}
