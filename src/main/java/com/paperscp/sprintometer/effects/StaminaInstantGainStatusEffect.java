package com.paperscp.sprintometer.effects;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.paperscp.sprintometer.effects.StaminaGainStatusEffect.SPRINT_GAIN_EFFECT_IDENTIFIER;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

public class StaminaInstantGainStatusEffect extends InstantStatusEffect {

    public StaminaInstantGainStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 16562691);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        if (entity.isPlayer() && entity instanceof ServerPlayerEntity) {

            // Not using Configurator here as server does not need it
            int calculatedAmplifier = sprintConfig.staminaGainInstantBaseAmplifier << amplifier;

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(calculatedAmplifier);

            ServerPlayNetworking.send(((ServerPlayerEntity) entity), SPRINT_GAIN_EFFECT_IDENTIFIER, buf);
        }

    }
}
