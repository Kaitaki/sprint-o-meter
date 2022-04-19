package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.items.SprintPotion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    @Shadow
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {}

    @Inject(at = { @At("TAIL") }, method = { "registerDefaults" })
    private static void registerDefaults(CallbackInfo ci) {

        registerPotionRecipe(SprintPotion.STAMINA_INSTANTGAIN, Items.SUGAR, SprintPotion.STAMINA_GAIN);
        registerPotionRecipe(SprintPotion.STAMINA_GAIN, Items.GLOWSTONE_DUST, SprintPotion.STAMINA_GAIN_STRONG);

        registerPotionRecipe(Potions.SWIFTNESS, Items.HONEYCOMB, SprintPotion.STAMINA_INSTANTGAIN);
        registerPotionRecipe(SprintPotion.STAMINA_INSTANTGAIN, Items.GLOWSTONE_DUST, SprintPotion.STAMINA_INSTANTGAIN_STRONG);
    }
}
