package com.paperscp.sprintometer.client.gui;

import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.mixins.MinecraftClientInterfaceMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static com.paperscp.sprintometer.SprintOMeter.client;

@Environment(EnvType.CLIENT)
public class StaminaHudManager {
    private byte hudTimer = 0; // In Ticks
    private int posY = 0; // Stamina Icon Y Coordinate
    private byte actionTimer = 0; // 3 Tick Delay For Checking "is_sprinting" & "is_jumping"

    public int getNumberCoords() { return posY == 20 ? 28 : 0; }

    public void tick() {
        int Stamina = StaminaManager.stamina;

        if (isSprintingOrJumpingDelayed(1) || isSprintingOrJumpingDelayed(2) || Stamina != 100) {
            hudTimer = 23;
        } else if (hudTimer > 0) {
            hudTimer = (byte) (hudTimer - 1);
        }
    }

    public int getNumberColor() {
        int Stamina = StaminaManager.stamina;

        return Stamina <= 25 ? 0xFFFF00 : 0xFFFFFF;
    }

    public int getIconCoords() {
        int a = ((MinecraftClientInterfaceMixin)client).getCurrentFPS();
        double b = 140.0 / a;
        int c = (int) Math.round(b);

        int Stamina = StaminaManager.stamina;

        if (Stamina != 100) {
            if (posY == 20) {return posY;}
            if (posY < 20) { posY = (posY + c); }
            if (posY > 20) { posY = 20; }

            return posY;

        } else if (hudTimer > 0) {

            return posY;

        } else {
            if (posY == 0) { return posY; }
            if (posY > 0) { posY = (posY - c); }
            if (posY < 0) { posY = 0; }

            return posY;
        }
    }

    private boolean isSprintingOrJumpingDelayed(int ix) {

        boolean is_sprinting = client.player.isSprinting();
        boolean is_jumping = StaminaManager.isJumpKeyPressed;

        if (actionTimer == 4) { actionTimer = 0; return ix == 1 ? is_sprinting : is_jumping; }
        else {actionTimer++; return false;}
    }

    public int getStaminaValue() {
        return StaminaManager.stamina < 0 ? 0 : Math.min(StaminaManager.stamina, 100);

    }
}
