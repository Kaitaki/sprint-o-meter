package com.paperscp.sprintometer.server;

import com.paperscp.sprintometer.SprintOConfig;
import com.paperscp.sprintometer.client.ActionStamina;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SprintOMeterServer implements ModInitializer {

    public static String MOD_ID = "sprintometer";
    public static final Logger logger = LogManager.getLogger("SprintOMeter");

    public static SprintOConfig sprintConfig;

    @Override
    public void onInitialize() {

        // Config Init
        SprintOConfig.configInit();

    }
}
