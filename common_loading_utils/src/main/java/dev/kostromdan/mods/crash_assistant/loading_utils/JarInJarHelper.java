package dev.kostromdan.mods.crash_assistant.loading_utils;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Core;
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
            ProcessHandle currentProcess = ProcessHandle.current();
            String currentProcessData = Objects.toString(currentProcess.pid()) + "_"
                    + Objects.toString(currentProcess.info().startInstant().get().getEpochSecond());
            Path extractedJarPath = extractJarInJar("app.jar", currentProcessData + "_app.jar");

            Optional<String> javaBinary = currentProcess.info().command();
            if (javaBinary.isEmpty()) {
                throw new IllegalStateException("Unable to determine the java binary path of current JVM. Crash Assistant won't work.");
            }

            ProcessBuilder crashAssistantAppProcessBuilder = new ProcessBuilder(
                    javaBinary.get(),
                    "-XX:+UseG1GC",
                    "-XX:MaxHeapFreeRatio=30",
                    "-XX:MinHeapFreeRatio=10",
                    "-XX:MaxGCPauseMillis=10000",
                    "-Xms8m",
                    "-Xmx256m",
                    "-jar", extractedJarPath.toAbsolutePath().toString(),
                    "-parentPID", Objects.toString(ProcessHandle.current().pid()),
                    "-log4jApi", LibrariesJarLocator.getLibraryJarPath(LogManager.class),
                    "-log4jCore", LibrariesJarLocator.getLibraryJarPath(Core.class),
                    "-googleGson", LibrariesJarLocator.getLibraryJarPath(Gson.class),
                    "-nightConfigCore", LibrariesJarLocator.getLibraryJarPath(FileConfig.class),
                    "-nightConfigToml", LibrariesJarLocator.getLibraryJarPath(TomlFormat.class)
            );
            Process crashAssistantAppProcess = crashAssistantAppProcessBuilder.start();
            Path currentProcessDataPath = Paths.get("local", "crash_assistant", currentProcessData + ".info");
            try {
                Files.write(currentProcessDataPath, Long.toString(crashAssistantAppProcess.pid()).getBytes());
            } catch (IOException ignored) {
            }


        } catch (Exception e) {
            LOGGER.error("Error while launching GUI: ", e);
        }
    }

    static Path extractJarInJar(String embeddedName, String outputName) throws IOException {
        Path outputDirectory = Paths.get("local", "crash_assistant");
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        Path extractedJarPath = outputDirectory.resolve(outputName);

        Files.list(outputDirectory).forEach(path -> {
            String fileName = path.getFileName().toString();
            if (Files.isRegularFile(path) && fileName.endsWith("app.jar")) {
                String processInfo = fileName.split("_app.jar")[0];
                Path processInfoPath = outputDirectory.resolve(processInfo + ".info");
                try {
                    Files.deleteIfExists(path);
                    Files.deleteIfExists(processInfoPath);
                } catch (IOException e) {
                    if (Files.exists(processInfoPath)) {
                        if (CrashAssistantConfig.get("general.kill_old_app")) {
                            Long minecraft_pid = Long.parseLong(processInfo.split("_")[0]);
                            Long start_time = Long.parseLong(processInfo.split("_")[1]);
                            Long app_pid;
                            try {
                                app_pid = Long.parseLong(Files.readString(processInfoPath));
                            } catch (IOException ex) {
                                LOGGER.error("Error while reading " + processInfoPath + ". This should never happen:", ex);
                                throw new RuntimeException(ex);
                            }
                            Optional<ProcessHandle> minecraftProcess = ProcessHandle.of(minecraft_pid);
                            Optional<ProcessHandle> appProcess = ProcessHandle.of(app_pid);
                            if (appProcess.isPresent()
                                    && !(minecraftProcess.isPresent() && minecraftProcess.get().info().startInstant().get().getEpochSecond() == start_time)) {
                                LOGGER.warn("Closed old CrashAssistantApp process to prevent confusing the player with window containing information from old crash.");
                                appProcess.get().destroy();
                                new java.util.Timer().schedule(
                                        new java.util.TimerTask() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Files.deleteIfExists(path);
                                                    Files.deleteIfExists(processInfoPath);
                                                } catch (IOException ignored) {
                                                }
                                            }
                                        },
                                        5000
                                );
                            }
                        }
                    }
                }
            } else if (Files.isRegularFile(path) && fileName.endsWith(".info") && fileName.contains("_")) {
                String processInfo = fileName.split("\\.info")[0];
                if(!Files.exists(outputDirectory.resolve(processInfo + "_app.jar"))) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                    }
                }
            }
        });

        InputStream jarStream = JarInJarHelper.class.getResourceAsStream("/META-INF/jarjar/" + embeddedName);

        if (jarStream == null) {
            throw new FileNotFoundException("Could not find embedded JAR: " + embeddedName);
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
