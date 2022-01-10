package com.paperscp.sprintometer.client.ui;

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

    public StaminaRenderer(MinecraftClient client) {
        this.client = client;
        this.staminaHudManager = new StaminaHudManager();

    }

    public void render(MatrixStack stack) {
        ClientPlayerEntity player = client.player;

        int scaledWidth = this.client.getWindow().getScaledWidth() / 2;
        int scaledHeight = this.client.getWindow().getScaledHeight();

        // Display
        if (player != null && SprintOMeterServer.sprintConfig.enableSprintOMeter) {
            client.textRenderer.drawWithShadow(stack, String.valueOf(staminaHudManager.staminaValue()), scaledWidth - 115, scaledHeight - staminaHudManager.staminaNumberCoords(), staminaHudManager.staminaNumberColor());

            RenderSystem.setShaderTexture(0, STAMINA_OVERLAY_ICON);
                drawTexture(stack, scaledWidth - 112, scaledHeight - staminaHudManager.staminaIconCoords(), 0, 0, 18, 18, 18, 18);
        }

    }
}
