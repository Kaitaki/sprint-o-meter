package com.paperscp.sprintometer.server;

import com.paperscp.sprintometer.config.SprintOConfig;
import com.paperscp.sprintometer.events.AttackEntityHandler;
import com.paperscp.sprintometer.events.PlayerDodgeHandler;
import com.paperscp.sprintometer.registry.SprintRegistry;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.combatroll.api.event.ServerSideRollEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SprintOMeterServer implements ModInitializer {

    public static final String MOD_ID = "sprintometer";
    public static final Logger logger = LogManager.getLogger("SprintOMeter");

    public static SprintOConfig sprintConfig;

    @Override
    public void onInitialize() {

        // Init //

        SprintOConfig.initConfig();

        StaminaDebuff.initDebuff();

        // Register //

        SprintRegistry.registerAll();

        ServerSideRollEvents.PLAYER_START_ROLLING.register(new PlayerDodgeHandler());

        // Tick //

        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> StaminaDebuff.tick());

    }
}
