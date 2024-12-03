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
import java.util.Optional;

public interface JarInJarHelper {
    Logger LOGGER = LoggerFactory.getLogger("CrashAssistantJarInJarHelper");

    static void launchCrashAssistantApp() {
        try {
            Path extractedJarPath = extractJarInJar("app.jar");

            ProcessHandle currentProcess = ProcessHandle.current();
            Optional<String> javaBinary = currentProcess.info().command();

            if (javaBinary.isEmpty()) {
                throw new IllegalStateException("Unable to determine the java binary path of current JVM. Crash Assistant won't work.");
            }

            ProcessBuilder crashAssistantAppProcess = new ProcessBuilder(
                    javaBinary.get(), "-jar", extractedJarPath.toAbsolutePath().toString(),
                    "-parentPID", Objects.toString(currentProcess.pid()),
                    "-Xmx1024m"
            );
            crashAssistantAppProcess.start();

        } catch (Exception e) {
            LOGGER.error("Error while launching GUI: ", e);
        }
    }

    static Path extractJarInJar(String name) throws IOException {
        Path outputDirectory = Paths.get("local", "crash_assistant");
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        Path extractedJarPath = outputDirectory.resolve(name);

        try {
            Files.deleteIfExists(extractedJarPath);
        } catch (IOException e) {
            LOGGER.warn("Error while deleting App jar, seems like GUI from prev. launch is still running: ", e);
        }

        InputStream jarStream = JarInJarHelper.class.getResourceAsStream("/META-INF/jarjar/" + name);

        if (jarStream == null) {
            throw new FileNotFoundException("Could not find embedded JAR: " + name);
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

    static Path getJarInJar(String name) throws IOException, URISyntaxException {
        //Idea taken from org.sinytra.connector.locator.EmbeddedDependencies#getJarInJar
        Path pathInModFile = Path.of(JarInJarHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).resolve("META-INF/jarjar/" + name);
        URI filePathUri = new URI("jij:" + pathInModFile.toAbsolutePath().toUri().getRawSchemeSpecificPart()).normalize();
        Map<String, ?> outerFsArgs = ImmutableMap.of("packagePath", pathInModFile);
        FileSystem zipFS = FileSystems.newFileSystem(filePathUri, outerFsArgs);
        return zipFS.getPath("/");
    }
}
