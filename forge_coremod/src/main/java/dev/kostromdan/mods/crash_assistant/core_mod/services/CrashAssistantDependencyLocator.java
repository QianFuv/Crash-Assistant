package dev.kostromdan.mods.crash_assistant.core_mod.services;

import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModProvider;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.kostromdan.mods.crash_assistant.loading_utils.JarExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Since forge doesn't load jar in jar mods from coremods, we should do it by ourselves.
 */
public class CrashAssistantDependencyLocator extends AbstractJarFileModProvider implements IDependencyLocator {
    public static final Logger LOGGER = LoggerFactory.getLogger("CrashAssistantDependencyLocator");

    @Override
    public List<IModFile> scanMods(Iterable<IModFile> loadedMods) {
        List<IModFile> mods = new ArrayList<>();
        try {
            mods.add(createMod(JarExtractor.getFromCoreMod("mod.jar")).file());
        } catch (Exception e) {
            LOGGER.error("Error while extracting CrashAssistantMod.jar: ", e);
        }
        return mods;
    }

    @Override
    public String name() {
        return "crash_assistant";
    }

    @Override
    public void initArguments(Map<String, ?> map) {
    }
}
