package com.paperscp.sprintometer.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Mixin(MinecraftClient.class)
    public interface MinecraftClientInterfaceMixin {
        @Accessor("currentFps")
        int getCurrentFPS();
    }
}


