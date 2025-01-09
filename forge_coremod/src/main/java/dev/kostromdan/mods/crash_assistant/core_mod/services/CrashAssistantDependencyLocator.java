package dev.kostromdan.mods.crash_assistant.core_mod.services;

import dev.kostromdan.mods.crash_assistant.lang.LanguageProvider;
import dev.kostromdan.mods.crash_assistant.loading_utils.JarInJarHelper;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileModProvider;
import net.minecraftforge.forgespi.locating.IDependencyLocator;
import net.minecraftforge.forgespi.locating.IModFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (Environment.getCurrentEnvironment() == Environment.SERVER) {
            LOGGER.warn("Crash Assistant is client only mod. Prevented mod loading!");
            return mods;
        };
        try {
            mods.add(createMod(JarInJarHelper.getJarInJar("crash_assistant-forge.jar")).file());
        } catch (Exception e) {
            LOGGER.error("Error while loading crash_assistant-forge.jar from jar in jar: ", e);
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
