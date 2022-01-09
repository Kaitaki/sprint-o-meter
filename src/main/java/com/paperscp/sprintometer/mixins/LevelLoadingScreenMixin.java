package com.paperscp.sprintometer.mixins;

import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.paperscp.sprintometer.client.ActionStamina.*;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {
    @Inject(at = @At("TAIL"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

        if (!(Stamina == 100 || !multiplayerWarned)) {
            Stamina = 100; // Makes sure the previous world's Stamina amount doesn't pass over.

            multiplayerWarned = false; // Resets the multiplayer warning

        }

    }
}
