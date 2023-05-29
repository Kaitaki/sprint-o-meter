package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.StaminaManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SwordItemMixin {

    // Prevents mining a block
    /*
    @Inject(at = @At("HEAD"), method = "canMine", cancellable = true)
    public void canMine(CallbackInfoReturnable<Boolean> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(false); }
    }
*/
    @Inject(at = @At("HEAD"), method = "getHandSwingDuration", cancellable = true)
    public void sprintometer$getHandSwingDuration(CallbackInfoReturnable<Integer> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(16); }
    }
}
