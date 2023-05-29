package com.paperscp.sprintometer.server;

import com.paperscp.sprintometer.config.SprintConfigurator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.config.SprintConfigurator.getConfig;
import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class StaminaDebuff {

    private static boolean debuffActive;
    private static ServerPlayerEntity serverPlayerEntity;

    private static final Identifier SPRINT_DEBUFF_IDENTIFIER = new Identifier(MOD_ID, "sprintable");

    private static short debuffDelay; // Debuff Apply Delay
    private static byte initApplied = 0; // First Time Apply

    public static Identifier getSprintDebuffIdentifier() {return SPRINT_DEBUFF_IDENTIFIER;}

    public static void initDebuff() {

        ServerPlayNetworking.registerGlobalReceiver(SPRINT_DEBUFF_IDENTIFIER, (server, player, handler, buf, responseSender) -> {
            debuffActive = buf.readBoolean();

//            System.out.println(debuffActive);

            server.execute(() -> serverPlayerEntity = server.getPlayerManager().getPlayer(player.getUuid()));
        });

    }

    public static void tick() { // World Tick
        if (debuffActive) {
            debuffDelay++;

            if (serverPlayerEntity != null && (debuffDelay >= 150 || initApplied == 0)) {
                // Approximately 3 Second Duration

//                serverPlayerEntity.getEntityWorld().getDifficulty()

                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 66, 0));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 66, 0));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 66, 0));

                debuffDelay = 0; initApplied = 1;
            }

        } else { debuffDelay = 0; initApplied = 0; }
    }
}
