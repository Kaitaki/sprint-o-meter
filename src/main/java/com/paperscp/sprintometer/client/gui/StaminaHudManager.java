package com.paperscp.sprintometer.client.gui;

import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.paperscp.sprintometer.SprintOMeter.staminaManager;

@Environment(EnvType.CLIENT)
public class StaminaHudManager {

    private int maxStamina = SprintOMeterServer.sprintConfig.maximumStamina; // Get directly from config instead of Configurator/StaminaManager because neither initialized
    private final float[] staminaBarChunks = new float[181];

    public StaminaHudManager() {
        float chunkAmount = (float) (SprintOMeterServer.sprintConfig.maximumStamina / 181.0);
        staminaBarChunks[0] = chunkAmount;

        for (int i = 1; i <= 180; i++) {
            staminaBarChunks[i] = staminaBarChunks[i - 1] + chunkAmount;
        }
    }

    private void recalculateBarChunks() {
        float chunkAmount = (float) (maxStamina / 181.0); // 181, being the length of pixels on the bar
        staminaBarChunks[0] = chunkAmount;

        for (int i = 1; i <= 180; i++) {
            staminaBarChunks[i] = staminaBarChunks[i - 1] + chunkAmount;
        }
    }

    public void refreshMaxStamina() {
        maxStamina = staminaManager.getMaxStamina();
        recalculateBarChunks();
    }

    public int getBarCoords() {
        if (staminaManager == null) { return 100;}

        int stamina = getStaminaValue();

        if (stamina == staminaManager.getMaxStamina()) { return 181; }

        for (int i = 180; i >= 0; i--) {
            if (stamina > staminaBarChunks[i]) { return i;}
        }

        return 0;
    }

    public int getStaminaValue() {
        return staminaManager.getStamina() < 0 ? 0 : Math.min(staminaManager.getStamina(), maxStamina);

    }
}
