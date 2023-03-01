package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.StaminaManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "isSwimming", cancellable = true)
    public void sprintometer$isSwimming(CallbackInfoReturnable<Boolean> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(false); }
    }
}
