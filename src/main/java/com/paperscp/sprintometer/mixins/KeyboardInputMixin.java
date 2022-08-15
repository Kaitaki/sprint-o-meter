package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.StaminaManager;
import com.paperscp.sprintometer.config.ConfiguratorOptions;
import com.paperscp.sprintometer.config.SprintConfigurator;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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
        StaminaManager.isJumpKeyPressed = this.settings.jumpKey.isPressed();

        if (SprintConfigurator.getConfig(ConfiguratorOptions.JUMPDEBUFF) == 0) { return; }
        this.jumping = !StaminaManager.isOutOfStamina() && this.settings.jumpKey.isPressed();
    }

}
