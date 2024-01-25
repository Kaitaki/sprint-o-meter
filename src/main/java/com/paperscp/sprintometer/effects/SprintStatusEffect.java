package com.paperscp.sprintometer.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class SprintStatusEffect {

    public static final StatusEffect STAMINA_GAIN = new StaminaGainStatusEffect();
    public static final StatusEffect STAMINA_GAIN_INSTANT = new StaminaInstantGainStatusEffect();

    public static void registerStatusEffects() {
        rStatusEffects(STAMINA_GAIN, "stamina_gain");
        rStatusEffects(STAMINA_GAIN_INSTANT, "stamina_gain_instant");
    }

    private static void rStatusEffects(StatusEffect statusEffect, String id) {
        Identifier identifier = new Identifier(MOD_ID, id);
        Registry.register(Registries.STATUS_EFFECT, identifier, statusEffect);
    }
}
