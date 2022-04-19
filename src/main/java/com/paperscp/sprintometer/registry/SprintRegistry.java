package com.paperscp.sprintometer.registry;

import com.paperscp.sprintometer.effects.SprintStatusEffect;
import com.paperscp.sprintometer.items.SprintPotion;

public class SprintRegistry {
    public static void registerAll() {

        SprintStatusEffect.registerStatusEffects();
        SprintPotion.registerPotions();

    }
}
