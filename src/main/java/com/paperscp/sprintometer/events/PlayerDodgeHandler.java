package com.paperscp.sprintometer.events;

import com.paperscp.sprintometer.client.StaminaManager;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.combatroll.api.event.ServerSideRollEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerDodgeHandler implements ServerSideRollEvents.PlayerStartRolling {

    @Override
    public void onPlayerStartedRolling(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d) {
        StaminaManager.isRolling(true);
    }
}
