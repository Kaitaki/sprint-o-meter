package com.paperscp.sprintometer.client.gui;

import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.paperscp.sprintometer.SprintOMeter.staminaManager;

@Environment(EnvType.CLIENT)
public class StaminaHudManager {
//    private byte hudTimer = 0; // In Ticks
//    private int posY = 0; // Stamina Icon Y Coordinate
//    private byte actionTimer = 0; // 3 Tick Delay For Checking "is_sprinting" & "is_jumping"
    private int maxStamina = SprintOMeterServer.sprintConfig.maximumStamina; // Get directly from config instead of Configurator/StaminaManager because neither initialized

    private final float[] staminaBarChunks = new float[181];
    // private final int[] chunkCoords = {6, 11, 16, 21, 26, 31, 36, 41, 46, 51, 56, 61, 66, 71, 76, 81, 86, 91, 96, 101, 106, 111, 116, 121, 126, 131, 136, 141, 146, 151, 156, 161, 166, 171, 176, 181};

//    public int getNumberCoords() { return posY == 20 ? 28 : 0; }

    public StaminaHudManager() {
        float chunkAmount = (float) (SprintOMeterServer.sprintConfig.maximumStamina / 181.0);
        staminaBarChunks[0] = chunkAmount;

        for (int i = 1; i <= 180; i++) {
            staminaBarChunks[i] = staminaBarChunks[i - 1] + chunkAmount;
        }
    }

//    public void tick() {
//        int Stamina = StaminaManager.stamina;
//
//        if (isSprintingOrJumpingDelayed(1) || isSprintingOrJumpingDelayed(2) || Stamina != 100) {
//            hudTimer = 23;
//        } else if (hudTimer > 0) {
//            hudTimer = (byte) (hudTimer - 1);
//        }
//    }

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
        int stamina = getStaminaValue();

        if (stamina == staminaManager.getMaxStamina()) {return 181;}

//        System.out.println(Arrays.toString(staminaBarChunks));

        // if (stamina > staminaBarChunks[17]) { return chunkCoords[17];}
        for (int i = 180; i >= 0; i--) {
            if (stamina > staminaBarChunks[i]) { return i;}
        }

        return 0;
    }

//    public int getNumberColor() {
//        int Stamina = StaminaManager.stamina;
//
//        return Stamina <= 25 ? 0xFFFF00 : 0xFFFFFF;
//    }
//
//    public int getIconCoords() {
//        int a = ((MinecraftClientInterfaceMixin)client).getCurrentFPS();
//        double b = 140.0 / a;
//        int c = (int) Math.round(b);
//
//        int Stamina = StaminaManager.stamina;
//
//        if (Stamina != 100) {
//            if (posY == 20) {return posY;}
//            if (posY < 20) { posY = (posY + c); }
//            if (posY > 20) { posY = 20; }
//
//            return posY;
//
//        } else if (hudTimer > 0) {
//
//            return posY;
//
//        } else {
//            if (posY == 0) { return posY; }
//            if (posY > 0) { posY = (posY - c); }
//            if (posY < 0) { posY = 0; }
//
//            return posY;
//        }
//    }

//    private boolean isSprintingOrJumpingDelayed(int ix) {
//
//        boolean is_sprinting = client.player.isSprinting();
//        boolean is_jumping = StaminaManager.isJumpKeyPressed;
//
//        if (actionTimer == 4) { actionTimer = 0; return ix == 1 ? is_sprinting : is_jumping; }
//        else {actionTimer++; return false;}
//    }

    public int getStaminaValue() {
        return staminaManager.getStamina() < 0 ? 0 : Math.min(staminaManager.getStamina(), maxStamina);

    }
}
