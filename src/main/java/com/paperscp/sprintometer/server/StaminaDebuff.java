package com.paperscp.sprintometer.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class StaminaDebuff {

    private static boolean debuffActive;
    private static ServerPlayerEntity serverPlayerEntity;

    private static Identifier sprintDebuffIdentifier = new Identifier(MOD_ID, "sprintable");

    private static short i; // Debuff Apply Delay
    private static byte i2 = 0; // First Time Apply

    public static void debuffInit() {

        ServerPlayNetworking.registerGlobalReceiver(sprintDebuffIdentifier, (server, player, handler, buf, responseSender) -> {
            debuffActive = buf.readBoolean();

//            System.out.println(debuffActive);

            server.execute(() -> {
                serverPlayerEntity = server.getPlayerManager().getPlayer(player.getUuid());
            });
        });

    }

    public static void tick() { // World Tick
        if (debuffActive) {
            i++;

            if (serverPlayerEntity != null && (i >= 150 || i2 == 0)) {
                // Approximately 3 Second Duration
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 66, 0));
                i = 0; i2 = 1;
            }

        } else {i = 0; i2 = 0;}
    }
}
