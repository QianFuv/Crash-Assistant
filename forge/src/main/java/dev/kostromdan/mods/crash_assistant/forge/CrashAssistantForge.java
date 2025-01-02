package dev.kostromdan.mods.crash_assistant.forge;

import com.mojang.brigadier.CommandDispatcher;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CrashAssistant.MOD_ID)
public final class CrashAssistantForge {
    public CrashAssistantForge() {
        CrashAssistant.init();
    }

    @Mod.EventBusSubscriber(modid = CrashAssistant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents {
//        @SubscribeEvent
//        public static void RegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
//            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
//            dispatcher.register(Commands.literal("crash_assistant")
//                    .then(Commands.literal("modlist")
//                            .then(Commands.literal("save")
//                                    .executes(CrashAssistantCommands::saveModlist)
//                            ).then(Commands.literal("diff")
//                                    .executes(CrashAssistantCommands::showDiff)
//                            )
//                    ).then(Commands.literal("crash")
//                            .requires(c -> CrashAssistantConfig.get("crash_command.enabled"))
//                            .then(Commands.literal("game")
//                                    .executes(CrashAssistantCommands::crashClient)
//                            ).then(Commands.literal("jwm")
//                                    .executes(CrashAssistantCommands::crashJVM)
//                            ).then(Commands.literal("no_crash")
//                                    .executes(CrashAssistantCommands::noCrash)
//                            ).executes(CrashAssistantCommands::crashClient)
//                    )
//            );
//
//        }
    }
}
