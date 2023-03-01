package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.StaminaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    // Prevents mining a block
    /*
    @Inject(at = @At("HEAD"), method = "canMine", cancellable = true)
    public void canMine(CallbackInfoReturnable<Boolean> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(false); }
    }
*/
    @Inject(at = @At("HEAD"), method = "getMiningSpeedMultiplier", cancellable = true)
    public void sprintometer$getMiningSpeedMultiplier(CallbackInfoReturnable<Float> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(0f); }
    }
}
