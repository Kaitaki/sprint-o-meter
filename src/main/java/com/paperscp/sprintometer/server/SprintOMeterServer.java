package com.paperscp.sprintometer.server;

import com.paperscp.sprintometer.config.SprintOConfig;
import com.paperscp.sprintometer.registry.SprintRegistry;
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

        // Tick //

        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> StaminaDebuff.tick());

    }
}
