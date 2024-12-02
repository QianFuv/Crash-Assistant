package dev.kostromdan.mods.crash_assistant.loading_utils;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Map;
import java.util.Objects;

public interface JarExtractor {
    Logger LOGGER = LoggerFactory.getLogger("CrashAssistantJarExtractor");

    static void launchCrashAssistantApp() {
        try {
            Path extractedJarPath = extractFromCoreMod("app.jar");

            ProcessBuilder crashAssistantAppProcess = new ProcessBuilder(
                    "java", "-jar", extractedJarPath.toAbsolutePath().toString(),
                    "-parentPID", PIDHelper.getCurrentProcessID(),
                    "-Xmx1024m"
            );
            crashAssistantAppProcess.start();

        } catch (Exception e) {
            LOGGER.error("Error while launching GUI: ", e);
        }
    }

    static Path extractFromCoreMod(String jarInJarName) throws IOException {
        Path outputDirectory = Paths.get("local", "crash_assistant");
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        Path extractedJarPath = outputDirectory.resolve(jarInJarName);

        try {
            Files.deleteIfExists(extractedJarPath);
        } catch (IOException e) {
            LOGGER.warn("Error while deleting App jar, seems like GUI from prev. launch is still running: ", e);
        }

        InputStream jarStream = JarExtractor.class.getResourceAsStream("/META-INF/jarjar/" + jarInJarName);

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

    static Path getFromCoreMod(String jarInJarName) throws IOException, URISyntaxException {
        //Idea taken from org.sinytra.connector.locator.EmbeddedDependencies#getJarInJar
        Path pathInModFile = Path.of(JarExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).resolve("META-INF/jarjar/"+jarInJarName);
        URI filePathUri = new URI("jij:" + pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart()).normalize();
        Map<String, ?> outerFsArgs = ImmutableMap.of("packagePath", pathInModFile);
        FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
        return zipFS.getPath("/");
    }
}
