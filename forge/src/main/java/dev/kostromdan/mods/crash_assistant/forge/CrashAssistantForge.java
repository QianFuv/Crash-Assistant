package dev.kostromdan.mods.crash_assistant.forge;

import com.mojang.brigadier.CommandDispatcher;
import dev.kostromdan.mods.crash_assistant.CrashAssistant;
import dev.kostromdan.mods.crash_assistant.commands.CrashAssistantCommands;
import dev.kostromdan.mods.crash_assistant.events.CrashAssistantEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CrashAssistant.MOD_ID)
public final class CrashAssistantForge {
    public CrashAssistantForge() {
        CrashAssistant.init();
    }

    @Mod.EventBusSubscriber(modid = CrashAssistant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void RegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(CrashAssistantCommands.getCommands());
        }

        @SubscribeEvent
        public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            CrashAssistantEvents.onGameJoin();
        }
    }
}
