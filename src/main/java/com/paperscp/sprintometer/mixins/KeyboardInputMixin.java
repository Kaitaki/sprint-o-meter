package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.ActionStamina;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.*;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    @Mutable
    @Final
    @Shadow
    private final GameOptions settings;

    public KeyboardInputMixin(GameOptions settings) {
        this.settings = settings;
    }

    /**
     * @author PaperPisces/Scorpio
     * @reason Adding Debuff Option for Sprint O' Meter
     */
    @Overwrite
    public void tick(boolean slowDown) {
        this.pressingForward = this.settings.keyForward.isPressed();
        this.pressingBack = this.settings.keyBack.isPressed();
        this.pressingLeft = this.settings.keyLeft.isPressed();
        this.pressingRight = this.settings.keyRight.isPressed();
        this.movementForward = this.pressingForward == this.pressingBack ? 0.0F : (this.pressingForward ? 1.0F : -1.0F);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0F : (this.pressingLeft ? 1.0F : -1.0F);

        this.jumping = !ActionStamina.outOfStamina() && this.settings.keyJump.isPressed();
        ActionStamina.isJumpKeyPressed = this.settings.keyJump.isPressed();

        this.sneaking = this.settings.keySneak.isPressed();
        if (slowDown) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3D);
            this.movementForward = (float)((double)this.movementForward * 0.3D);
        }
    }

}
