package com.paperscp.sprintometer;

import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.client.gui.StaminaRenderer;
import com.paperscp.sprintometer.events.AttackEntityHandler;
import com.paperscp.sprintometer.networking.SprintNetworking;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// TODO: Difficulty System

@Environment(EnvType.CLIENT)
public class SprintOMeter implements ClientModInitializer {

    public static MinecraftClient client = null;
    public static StaminaRenderer staminaRenderer;
    public static StaminaManager staminaManager;

    @Override
    public void onInitializeClient() {

        // Init

        client = MinecraftClient.getInstance();
        staminaRenderer = new StaminaRenderer(client);

        SprintNetworking.registerPacketReceivers();
        SprintNetworking.registerConnectionEvents();

        ClientTickEvents.END_WORLD_TICK.register(client -> {
            staminaManager.tick();
            // SprintOMeter.staminaRenderer.staminaHudManager.tick();
        });

        //Events

        //Better Combat overrides the handSwinging method, so we instead need to check for attack events when it is loaded
        if (FabricLoader.getInstance().isModLoaded("bettercombat")) {

            String BCversion = FabricLoader.getInstance().getModContainer("bettercombat").get().getMetadata().getVersion().toString().substring(0, 3);

            if (!BCversion.contains("1.5") && !BCversion.contains("1.4") && !BCversion.contains("1.3") && !BCversion.contains("1.2")
                && !BCversion.contains("1.1") && !BCversion.contains("1.0")) {

                BetterCombatClientEvents.ATTACK_START.register(new AttackEntityHandler());

            }
        }

    }

    // Util //

    public static void displayChatMessage(String message, Formatting formatting) {
        client.inGameHud.getChatHud().addMessage(Text.literal("[Sprint O' Meter]: " + message).formatted(formatting));
    }

}
