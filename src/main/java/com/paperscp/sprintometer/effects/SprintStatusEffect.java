package com.paperscp.sprintometer.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
        Registry.register(Registry.STATUS_EFFECT, identifier, statusEffect);
    }
}
