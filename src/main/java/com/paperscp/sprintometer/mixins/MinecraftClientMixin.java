package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.SprintOMeter;
import com.paperscp.sprintometer.client.ActionStamina;
import com.paperscp.sprintometer.client.ui.StaminaHudManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        SprintOMeter.tick();

    }

    @Mixin(MinecraftClient.class)
    public interface MinecraftClientInterfaceMixin {
        @Accessor("currentFps")
        int getCurrentFPS();
    }
}


