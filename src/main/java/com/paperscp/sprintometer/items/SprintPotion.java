package com.paperscp.sprintometer.items;


import com.paperscp.sprintometer.effects.SprintStatusEffect;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

public class SprintPotion {

    public static Potion STAMINA_GAIN; // Not final as duration is dynamic
    public static Potion STAMINA_GAIN_STRONG;
    public static final Potion STAMINA_INSTANTGAIN = new Potion(new StatusEffectInstance(SprintStatusEffect.STAMINA_GAIN_INSTANT, 1));
    public static final Potion STAMINA_INSTANTGAIN_STRONG = new Potion(new StatusEffectInstance(SprintStatusEffect.STAMINA_GAIN_INSTANT, 1, 1));


    public static void registerPotions() {

        if (!SprintOMeterServer.sprintConfig.enablePotions) { return; }

        STAMINA_GAIN = new Potion(new StatusEffectInstance(SprintStatusEffect.STAMINA_GAIN, sprintConfig.staminaGainPotionDuration));
        STAMINA_GAIN_STRONG = new Potion(new StatusEffectInstance(SprintStatusEffect.STAMINA_GAIN, (int) Math.floor(sprintConfig.staminaGainPotionDuration / 2.0), 1));

        rPotions(STAMINA_GAIN, "stamina_gain");
        rPotions(STAMINA_GAIN_STRONG, "stamina_gain_strong");
        rPotions(STAMINA_INSTANTGAIN, "stamina_instantgain");
        rPotions(STAMINA_INSTANTGAIN_STRONG, "stamina_instantgain_strong");
    }

    private static void rPotions(Potion potion, String id) {
        Identifier identifier = new Identifier(MOD_ID, id);
        Registry.register(Registry.POTION, identifier, potion);

    }
}
