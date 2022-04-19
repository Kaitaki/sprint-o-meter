package com.paperscp.sprintometer;

import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.client.gui.StaminaRenderer;
import com.paperscp.sprintometer.config.SprintOConfig;
import com.paperscp.sprintometer.effects.StaminaGainStatusEffect;
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
import net.minecraft.util.Identifier;

import java.util.Arrays;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

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

        // Client Networking //

        // Config Packet Receiver && Stamina Refresh
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.CONFIG_VALUES_IDENTIFIER, (client, handler, buf, responseSender) -> {
            int[] configValues = buf.readIntArray();

            client.execute(() -> {
                System.out.println(Arrays.toString(configValues));
//                System.out.println(Arrays.toString(ConfigPacket.decodePacket(configValues, 7)));

                if (!client.isInSingleplayer()) {
                    SprintOMeterServer.logger.info("[Sprint O' Meter]: Switching to server config options..");
                }

                SprintOConfig.Configurator.setClientConfig(configValues);

                staminaManager.refreshStamina();
                staminaRenderer.staminaHudManager.refreshMaxStamina();
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID, "config-sync-check"), (client, handler, buf, responseSender) -> {
            int[] syncArray = buf.readIntArray();

            client.execute(() -> SprintOConfig.Configurator.checkClientConfig(syncArray));
        });

        ClientPlayNetworking.registerGlobalReceiver(StaminaGainStatusEffect.SPRINT_GAIN_EFFECT_IDENTIFIER, (client, handler, buf, responseSender) -> {
            int amplifier = buf.readInt();

            client.execute(() -> {

                if (staminaManager.getStamina() >= staminaManager.getMaxStamina()) { return; }

                staminaManager.addStamina(Math.abs(amplifier)); // Abs needed just in case integer overflow happens
            });
        });

        // Tick Events //

        ClientTickEvents.END_WORLD_TICK.register(client -> {
            staminaManager.tick();
            // SprintOMeter.staminaRenderer.staminaHudManager.tick();
        });


        // Connection Events //

        // Multiplayer Warn Message && Instantiate StaminaManager
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!MinecraftClient.getInstance().isInSingleplayer()) {
                displayChatMessage("Using server config options..", Formatting.GRAY);
            }

            staminaManager = new StaminaManager();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> staminaManager = null);

    }

    // Util //

    public static void displayChatMessage(String message, Formatting formatting) {
        client.inGameHud.getChatHud().addMessage(new LiteralText("[Sprint O' Meter]: " + message).formatted(formatting));
    }

}
