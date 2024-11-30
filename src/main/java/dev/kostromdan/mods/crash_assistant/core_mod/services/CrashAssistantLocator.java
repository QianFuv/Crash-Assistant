package dev.kostromdan.mods.crash_assistant.core_mod.services;

import dev.kostromdan.mods.crash_assistant.core_mod.utils.JarExtractor;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModProvider;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CrashAssistantLocator extends AbstractJarFileModProvider implements IDependencyLocator {
    public static final Logger LOGGER = LoggerFactory.getLogger("CrashAssistantLocator");

    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        try{
        List<IModFile> mods = new ArrayList<>();

        Path modPath = null;
        try {
            modPath =  JarExtractor.extractFromCoreMod("CrashAssistantMod.jar");
        } catch (Exception e) {
            LOGGER.error("Error while extracting CrashAssistantMod.jar", e);
        }
        IModFile modFile = createMod(modPath).file();

        mods.add(modFile);

        return mods;
        } catch (Exception e) {
            LOGGER.error("Error while scanMods: ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public String name() {
        return "crash_assistant";
    }

    @Override
    public void initArguments(Map<String, ?> map) {}
}
