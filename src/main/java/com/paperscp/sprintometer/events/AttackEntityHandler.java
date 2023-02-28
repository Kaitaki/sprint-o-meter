package com.paperscp.sprintometer.events;

import com.paperscp.sprintometer.client.StaminaManager;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.client.network.ClientPlayerEntity;

public class AttackEntityHandler implements BetterCombatClientEvents.PlayerAttackStart {

    @Override
    public void onPlayerAttackStart(ClientPlayerEntity clientPlayerEntity, AttackHand attackHand) {
        StaminaManager.isAttacking(true);
        //System.out.println("We are Better Combat swinging: ");
    }
}
