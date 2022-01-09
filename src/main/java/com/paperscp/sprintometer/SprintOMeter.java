package com.paperscp.sprintometer;

import com.paperscp.sprintometer.client.ActionStamina;
import com.paperscp.sprintometer.client.ui.StaminaRenderer;
import com.paperscp.sprintometer.networking.ConfigPacket;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.Arrays;

// TODO: Optimize & Port Over to Alchemic

@Environment(EnvType.CLIENT)
public class SprintOMeter implements ClientModInitializer {

    public static MinecraftClient client = null;
    public static StaminaRenderer staminaRenderer;

    @Override
    public void onInitializeClient() {
        // Final Global Variables
        client = MinecraftClient.getInstance();

        // Config Packet Data Receiver
        ClientPlayNetworking.registerGlobalReceiver(ConfigPacket.configValuesIdentifier, (client, handler, buf, responseSender) -> {
            byte[] configValues = buf.readByteArray();

            SprintOMeterServer.logger.info("Switching to server config options..");

            client.execute(() -> {
//                System.out.println(Arrays.toString(configValues));
//                System.out.println(Arrays.toString(ConfigPacket.decodePacket(configValues, 7)));
                ActionStamina.packetSetter(ConfigPacket.decodePacket(configValues, 7));
            });
        });

        // Stamina Meter Renderer
        staminaRenderer = new StaminaRenderer(client);


    }

    public static void tick() { // There's probably a better way to do this than a Mixin but this will do for now
        if (client.player != null) {
            ActionStamina.tick();
            SprintOMeter.staminaRenderer.staminaHudManager.staminaHudDelay();
        }
    }

}
