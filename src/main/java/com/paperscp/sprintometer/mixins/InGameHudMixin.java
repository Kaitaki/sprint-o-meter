package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.SprintOMeter;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawableHelper {
    @Inject(at = @At("TAIL"), method = "render")
    public void sprintometer$render(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
        SprintOMeter.staminaRenderer.render(matrixStack);
    }
}
