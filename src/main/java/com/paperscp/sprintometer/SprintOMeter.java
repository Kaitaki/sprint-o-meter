package com.paperscp.sprintometer;

import com.paperscp.sprintometer.client.ActionStamina;
import com.paperscp.sprintometer.client.gui.StaminaRenderer;
import com.paperscp.sprintometer.config.SprintOConfig;
import com.paperscp.sprintometer.networking.ConfigPacket;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

// TODO: Optimize New Drowning & Stamina Bug Fix Additions + Difficulty System

@Environment(EnvType.CLIENT)
public class SprintOMeter implements ClientModInitializer {

    public static MinecraftClient client = null;
    public static StaminaRenderer staminaRenderer;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();

        // Config Packet Data Receiver
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.configValuesIdentifier, (client, handler, buf, responseSender) -> {
            byte[] configValues = buf.readByteArray();

            client.execute(() -> {
//                System.out.println(Arrays.toString(configValues));
//                System.out.println(Arrays.toString(ConfigPacket.decodePacket(configValues, 7)));
                SprintOMeterServer.logger.info("Switching to server config options..");
                SprintOConfig.Configurator.packetSetter(configValues);
            });
        });

        // Stamina Meter Renderer
        staminaRenderer = new StaminaRenderer(client);

        ClientTickEvents.END_WORLD_TICK.register(client -> {
            ActionStamina.tick();
            SprintOMeter.staminaRenderer.staminaHudManager.staminaHudDelay();
        });

        // Multiplayer Warn Message
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client1) -> {
            if (!MinecraftClient.getInstance().isInSingleplayer()) { // Multiplayer Notice
                client.inGameHud.getChatHud().addMessage(new LiteralText("[Sprint O' Meter]: Using server config options..").formatted(Formatting.GRAY));
            }
        });

    }

}
