package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.SprintOMeter.client;
import static com.paperscp.sprintometer.config.ConfiguratorOptions.*;
import static com.paperscp.sprintometer.config.SprintOConfig.Configurator.configurator;

@Environment(EnvType.CLIENT)
public class ActionStamina {

    public static int Stamina = 100;
    public static boolean isJumpKeyPressed;

    private static byte i = 0; // Cooldown Delay
    private static byte i2 = 0; // Stamina Deduction Delay
    private static byte i3 = 0; // Stamina Restoration Delay
    private static byte i4 = 0; // Stamina Debuff Delay
    private static boolean b1 = false; // Stamina Debuff Switch
    private static byte i5 = 0; // Check If Enabled Delay
    private static byte bb2 = 1; // Check If Enabled Cache

    private static final Identifier sprintDebuffIdentifier = new Identifier(SprintOMeterServer.MOD_ID, "sprintable");

//    public static byte temp(int ix) { // For Debug Menu in StaminaRenderer
//        return switch (ix) {
//            case 1 -> i;
//            case 2 -> i2;
//            case 3 -> i3;
//            case 4 -> i4;
//            case 5 -> (byte) (b1 ? 1 : 0);
//            case 6 -> i5;
//            case 7 -> bb2;
//            default -> (byte) 0;
//        };
//    }

    public static void tick() {

        ClientPlayerEntity player = client.player;

        boolean isSprinting = player.isSprinting();
        boolean isJumping = isJumpKeyPressed;
        boolean isInWater = player.isSubmergedInWater();

        if (staminaIneligible(player)) {
            if (i5 == 0) { bb2 = configurator(ISENABLED); i5 = 4; return; }
            i5--;

            if (!b1) { return; } // If Debuff Active..

            if (bb2 == 0) { // & User Disables Mod
                b1 = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
                Stamina = 100;
            }

            if (player.isCreative() || player.isSpectator()) { // & User Switches Gamemodes
                b1 = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
            }

            return;
        }

        staminaRestoration(isSprinting, isJumping, isInWater);

        if (i2 == 0) {
            staminaDeduction(isSprinting, isJumping);
            i2 = configurator(STAMINADEDUCTIONDELAY);
            if (Stamina <= 25 && (isSprinting || isJumping) && configurator(LOWSTAMINAPING) == 1) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2);
            }
        } else { i2--; }

        if (i4 == 0) {
            if (Stamina <= 0 && !b1) {
                b1 = true;

                activateDebuff();
            }
            if (Stamina > 0 && b1) {
                b1 = false;

                deactivateDebuff();
            }
            i4 = 20;
        } else { i4--; }

    }

    private static void activateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        ClientPlayNetworking.send(sprintDebuffIdentifier, buf);
    }

    private static void deactivateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        ClientPlayNetworking.send(sprintDebuffIdentifier, buf);
    }

    public static boolean outOfStamina() { return b1; } // For Debuff in KeyboardInputMixin

    private static void staminaDeduction(boolean isSprinting, boolean isJumping) {
        if (!isSprinting && !isJumping) { return; }
        if (Stamina == 0 || Stamina < 0) { Stamina = 0; return; }

        if (isSprinting) { Stamina = Stamina - configurator(SPRINTDEDUCTIONAMOUNT); i = configurator(COOLDOWNDELAY); } // Sprint Deduct

        if (isJumping) { Stamina = Stamina - configurator(JUMPDEDUCTIONAMOUNT); i = configurator(COOLDOWNDELAY); } // Jump Deduct
    }

    private static void staminaRestoration(boolean isSprinting, boolean isJumping, boolean isInWater) {
        if (isSprinting || isJumping) { return; }
        if (isInWater) { staminaRestorationAlternate(); return; }
        if (i != 0) { i--; return; } // Cooldown Delay
        if (Stamina == 100) { return; }

        if (i3 != 0) { i3--; return; } // Stamina Restoration Delay

        Stamina = Stamina + configurator(STAMINARESTORATIONAMOUNT);
        i3 = configurator(STAMINARESTORATIONDELAY);
        if (Stamina > 100) {
            Stamina = 100;
        }
    }

    private static void staminaRestorationAlternate() {
        if (i != 0) { i--; return; } // Cooldown Delay
        if (Stamina == 100) { return; }

        if (i3 != 0) { i3--; return; } // Stamina Restoration Delay

        Stamina = Stamina + configurator(STAMINARESTORATIONAMOUNT);
        i3 = (byte) (configurator(STAMINARESTORATIONDELAY) + 2);
        if (Stamina > 100) {
            Stamina = 100;
        }
    }

    // Util
    public static boolean staminaIneligible(ClientPlayerEntity player) {
        return player.isCreative() || player.isSpectator() || isRidingVehicle(player)
                || configurator(ISENABLED) == 0 || client.isPaused() || player.isDead();
    }

    private static boolean isRidingVehicle(ClientPlayerEntity player) {
        return player.getVehicle() != null;
    }
}
