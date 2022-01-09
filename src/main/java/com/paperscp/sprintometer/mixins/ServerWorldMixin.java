package com.paperscp.sprintometer.mixins;

import com.paperscp.sprintometer.SprintOConfig;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(at = @At("TAIL"), method = "onPlayerConnected")
    public void onPlayerConnect(ServerPlayerEntity player, CallbackInfo ci) {
        if (!player.getServer().isSingleplayer()) {
            System.out.println("(SprintOMeter) Sending server config options to " + player.getEntityName() + "..");
            new SprintOConfig().sendConfigPackets(player);
        }

    }
}


