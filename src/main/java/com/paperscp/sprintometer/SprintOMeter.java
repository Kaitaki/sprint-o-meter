package com.paperscp.sprintometer;

import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.client.gui.StaminaRenderer;
import com.paperscp.sprintometer.networking.SprintNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

// TODO: Difficulty System + Saturation Dependency

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

    }

    // Util //

    public static void displayChatMessage(String message, Formatting formatting) {
        client.inGameHud.getChatHud().addMessage(new LiteralText("[Sprint O' Meter]: " + message).formatted(formatting));
    }

}
