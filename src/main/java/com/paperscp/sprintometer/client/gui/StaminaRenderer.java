package com.paperscp.sprintometer.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paperscp.sprintometer.client.ActionStamina;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StaminaRenderer extends DrawableHelper {

    private static final Identifier STAMINA_OVERLAY_ICON = new Identifier("minecraft", "textures/mob_effect/speed.png");
    private final MinecraftClient client;
    public final StaminaHudManager staminaHudManager;
    private byte tempCache;

    public StaminaRenderer(MinecraftClient client) {
        this.client = client;
        this.staminaHudManager = new StaminaHudManager();

    }

//    private String temp(int i) { // For Stamina Renderer
//        return switch (i) {
//            case 1 -> "Cooldown: ";
//            case 2 -> "Stamina Deduction: ";
//            case 3 -> "Stamina Restoration: ";
//            case 4 -> "Stamina Debuff: ";
//            case 5 -> "Stamina Debuff Switch: ";
//            case 6 -> "Check If Enabled Delay: ";
//            case 7 -> "Check If Enabled Cache: ";
//            default -> "ERR: ";
//        };
//    }

    public void render(MatrixStack stack) {
        ClientPlayerEntity player = client.player;

        int scaledWidth = this.client.getWindow().getScaledWidth() / 2;
        int scaledHeight = this.client.getWindow().getScaledHeight();

//        short temp = 40;
//
//        for (short i = 1; i < 8; i++) { // Stamina Renderer
//
//            client.textRenderer.draw(stack, this.temp(i) + ActionStamina.temp(i), scaledWidth - 175, scaledHeight - temp, 0xFFFFFF);
//            temp = (short) (temp + 20);
//        }

        // Display
        if (player != null && SprintOMeterServer.sprintConfig.enableSprintOMeter && !player.isCreative() && !player.isSpectator()) {

            if (tempCache == 0 && ActionStamina.Stamina == 100) { return; }

            client.textRenderer.drawWithShadow(stack, String.valueOf(staminaHudManager.staminaValue()), scaledWidth - 115, scaledHeight - staminaHudManager.staminaNumberCoords(), staminaHudManager.staminaNumberColor());

            RenderSystem.setShaderTexture(0, STAMINA_OVERLAY_ICON);
                int staminaIconCoords = staminaHudManager.staminaIconCoords();
                drawTexture(stack, scaledWidth - 112, scaledHeight - staminaIconCoords, 0, 0, 18, 18, 18, 18);
            tempCache = (byte) staminaIconCoords;
        }

    }
}
