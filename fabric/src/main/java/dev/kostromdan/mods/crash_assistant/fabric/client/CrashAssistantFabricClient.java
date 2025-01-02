package dev.kostromdan.mods.crash_assistant.fabric.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public final class CrashAssistantFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("crash_assistant")
                    .then(ClientCommandManager.literal("modlist")
                            .then(ClientCommandManager.literal("save")
                                    .executes(CrashAssistantCommands::saveModlist)
                            ).then(ClientCommandManager.literal("diff")
                                    .executes(CrashAssistantCommands::showDiff)
                            )
                    ).then(addCrashCommands())
            );
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> addCrashCommands() {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal("crash");
        builder.requires(c -> CrashAssistantConfig.get("crash_command.enabled"));
        HashMap<String, String> commands = new HashMap<>() {{
            put("game", "Minecraft");
            put("jwm", "JVM");
            put("no_crash", "noCrash");
        }};
        for (Map.Entry<String, String> s : commands.entrySet()) {
            builder.then(addCrashArguments(ClientCommandManager.literal(s.getKey()), s.getValue()));
        }
        return builder;
    }


    private static LiteralArgumentBuilder<FabricClientCommandSource> addCrashArguments(LiteralArgumentBuilder<FabricClientCommandSource> builder, String toCrash) {
        builder.executes(context -> CrashAssistantCommands.crash(toCrash, new HashSet<>()));
        addArgumentsRecursively(builder, new HashSet<>(), toCrash);
        return builder;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> addArgumentsRecursively(
            LiteralArgumentBuilder<FabricClientCommandSource> builder,
            HashSet<String> currentArgs,
            String toCrash) {
        if (currentArgs.size() == CrashAssistantCommands.supportedCrashArgs.size()) {
            return builder;
        }

        for (String arg : CrashAssistantCommands.supportedCrashArgs) {
            if (currentArgs.contains(arg)) continue;
            if (Objects.equals(arg, "--GCBeforeHeapDump") && !currentArgs.contains("--withHeapDump")) continue;

            HashSet<String> newArgs = new HashSet<>(currentArgs);
            newArgs.add(arg);

            LiteralArgumentBuilder<FabricClientCommandSource> subCommand = ClientCommandManager.literal(arg);
            subCommand = subCommand.executes(context -> CrashAssistantCommands.crash(toCrash, new HashSet<>(newArgs)));

            addArgumentsRecursively(subCommand, newArgs, toCrash);
            builder.then(subCommand);
        }
        return builder;
    }
}