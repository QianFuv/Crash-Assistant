package dev.kostromdan.mods.crash_assistant_app.utils;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

public interface FileUtils {
    static void addIfExists(Map<String, Path> map, Path path) {
        addIfExists(map, path.getFileName().toString(), path);
    }

    static void addIfExists(Map<String, Path> map, String fileName, Path path) {
        if (Files.exists(path) && Files.isRegularFile(path)) {
            map.put(fileName, path);
        }
    }

    static void removeTmpFiles(Path dir){
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".tmp")) {
                        Files.delete(file);
                        System.out.println("Deleted: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Error while deleting tmp files: ", e);
        }
    }

}
