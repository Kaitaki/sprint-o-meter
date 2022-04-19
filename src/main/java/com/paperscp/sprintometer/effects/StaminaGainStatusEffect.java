package com.paperscp.sprintometer.effects;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

public class StaminaGainStatusEffect extends StatusEffect {

    public static final Identifier SPRINT_GAIN_EFFECT_IDENTIFIER = new Identifier(MOD_ID, "sprint-gain-effect");

    private int effectDelay = 0;

    private LivingEntity entityWithStatusEffect = null; // To make sure that it is only effective on players

    public StaminaGainStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 15451486);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (entityWithStatusEffect == null) { return true; }

        if (!entityWithStatusEffect.isPlayer()) { entityWithStatusEffect.removeStatusEffect(SprintStatusEffect.STAMINA_GAIN); }

        if (effectDelay == sprintConfig.staminaGainEffectDelay) { effectDelay = 0; return true; }
        effectDelay++;
        return false;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entityWithStatusEffect = entity;

        if (entity.isPlayer() && entity instanceof ServerPlayerEntity) {

            // Not using Configurator here as server does not need it
            int calculatedAmplifier = sprintConfig.staminaGainBaseAmplifier << amplifier;

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(calculatedAmplifier);

            ServerPlayNetworking.send(((ServerPlayerEntity) entity), SPRINT_GAIN_EFFECT_IDENTIFIER, buf);
        }
    }
}
