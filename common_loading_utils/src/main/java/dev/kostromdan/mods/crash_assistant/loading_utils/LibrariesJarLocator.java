package dev.kostromdan.mods.crash_assistant.loading_utils;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface LibrariesJarLocator {
    static Path getLibraryJarPath(Class cls) throws URISyntaxException, RuntimeException, FileNotFoundException {
        String pathString = cls.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        int jarIndex = pathString.lastIndexOf(".jar");
        if (jarIndex == -1) {
            throw new RuntimeException("Not found '.jar' in IRI.getPath() of `" + cls + "'; path: " + pathString);
        }

        pathString = pathString.substring(0, jarIndex + 4);
        if (pathString.startsWith("/")) {
            pathString = pathString.substring(1);
        }

        Path path;
        try {
            path = Paths.get(pathString);
        } catch (Exception e) {
            throw new RuntimeException("Failed converting pathString got from `" + cls + "' to Paths.get(pathString); pathString: `" + pathString + "`");
        }

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Successfully parsed '.jar' path of `" + cls + "',but it does not exist; path: `" + path + "`; pathString: `" + pathString + "`");
        }
        return path;
    }
}
