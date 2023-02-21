package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.client.StaminaManager;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {
    // Prevents mining a block
    /*
    @Inject(at = @At("HEAD"), method = "canMine", cancellable = true)
    public void canMine(CallbackInfoReturnable<Boolean> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(false); }
    }
*/
    @Inject(at = @At("HEAD"), method = "getMiningSpeedMultiplier", cancellable = true)
    public void getMiningSpeedMultiplier(CallbackInfoReturnable<Float> ci) {
        if (StaminaManager.isOutOfStamina()) { ci.setReturnValue(0f); }
    }
}
