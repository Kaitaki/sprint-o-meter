package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.SprintOMeter;
import com.paperscp.sprintometer.client.ActionStamina;
import com.paperscp.sprintometer.config.ConfiguratorOptions;
import com.paperscp.sprintometer.config.SprintOConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
    @Mutable
    @Final
    @Shadow
    private final GameOptions settings;

    public KeyboardInputMixin(GameOptions settings) {
        this.settings = settings;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(boolean slowDown, CallbackInfo ci) {
        ActionStamina.isJumpKeyPressed = this.settings.keyJump.isPressed();

        this.jumping = !ActionStamina.outOfStamina() && this.settings.keyJump.isPressed();
    }

}
