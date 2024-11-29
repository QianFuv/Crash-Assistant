package dev.kostromdan.mods.crash_assistant.core_mod.services;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import dev.kostromdan.mods.crash_assistant.core_mod.utils.JarExtractor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class CrashAssistantTransformationService implements ITransformationService {
    public static final Logger LOGGER = LoggerFactory.getLogger("CrashAssistantCoreMod");

    static {
        LOGGER.info("Launching CrashAssistantApp");
        JarExtractor.launchCrashAssistantApp();
    }
    @Override
    public @NotNull String name() {
        return "crash_assistant";
    }

    @Override
    public void initialize(IEnvironment environment) {}

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {}

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }
}
