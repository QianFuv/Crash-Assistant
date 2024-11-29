package dev.kostromdan.mods.crash_assistant.core_mod.utils;

import dev.kostromdan.mods.crash_assistant.core_mod.services.CrashAssistantTransformationService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public interface JarExtractor {

    static void launchCrashAssistantApp() {
        try {
            Path extractedJarPath = extractFromCoreMod("CrashAssistantApp.jar");

            ProcessBuilder crashAssistantAppProcess = new ProcessBuilder(
                    "java", "-jar", extractedJarPath.toAbsolutePath().toString(),
                    "-parentPID", Objects.toString(PIDHelper.getCurrentProcessID()),
                    "-Xmx1024m"
            );
            crashAssistantAppProcess.start();

        } catch (Exception e) {
            CrashAssistantTransformationService.LOGGER.error("Error while launching GUI: ", e);
        }
    }

    static Path extractFromCoreMod(String jarInJarName) throws IOException {
        Path outputDirectory = Paths.get("local", "crash_assistant");
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        Path extractedJarPath = outputDirectory.resolve(jarInJarName);

        Files.deleteIfExists(extractedJarPath);

        InputStream jarStream = CrashAssistantTransformationService.class.getResourceAsStream("/META-INF/jarjar/" + jarInJarName);

        if (jarStream == null) {
            throw new FileNotFoundException("Could not find embedded JAR: " + jarInJarName);
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
