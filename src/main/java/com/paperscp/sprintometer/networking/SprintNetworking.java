package com.paperscp.sprintometer.networking;

import com.paperscp.sprintometer.SprintOMeter;
import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.config.SprintConfigurator;
import com.paperscp.sprintometer.effects.StaminaGainStatusEffect;
import com.paperscp.sprintometer.networking.config.ConfigPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class SprintNetworking {

    public static void registerConnectionEvents() {
        // Config Packet Receiver && Stamina Refresh
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.CONFIG_VALUES_IDENTIFIER, (client, handler, buf, responseSender) -> {
            int[] configValues = buf.readIntArray();

            client.execute(() -> {
                System.out.println(Arrays.toString(configValues));
//                System.out.println(Arrays.toString(ConfigPacket.decodePacket(configValues, 7)));

                SprintConfigurator.setClientConfig(configValues);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(MOD_ID, "config-sync-check"), (client, handler, buf, responseSender) -> {
            int[] syncArray = buf.readIntArray();

            client.execute(() -> SprintConfigurator.checkClientConfig(syncArray));
        });

        ClientPlayNetworking.registerGlobalReceiver(StaminaGainStatusEffect.SPRINT_GAIN_EFFECT_IDENTIFIER, (client, handler, buf, responseSender) -> {
            int amplifier = buf.readInt();

            client.execute(() -> SprintOMeter.staminaManager.applyStatusEffect(1, amplifier));
        });
    }

    public static void registerPacketReceivers() {
        // Multiplayer Warn Message && Instantiate StaminaManager
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SprintOMeter.staminaManager = new StaminaManager();

            if (!MinecraftClient.getInstance().isInSingleplayer()) {
                SprintOMeter.displayChatMessage("Using server config options..", Formatting.GRAY);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SprintOMeter.staminaManager = null);
    }
}
