package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.SprintOMeter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At("TAIL"), method = "render")
    public void render(DrawContext context, float tickDelta, CallbackInfo info) {
        SprintOMeter.staminaRenderer.render(context);
    }
}
