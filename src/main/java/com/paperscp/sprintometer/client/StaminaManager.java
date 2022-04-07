package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.server.StaminaDebuff;
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
import static com.paperscp.sprintometer.config.SprintOConfig.Configurator.getConfig;

@Environment(EnvType.CLIENT)
public class StaminaManager {

    public static int stamina = 100;
    public static boolean isJumpKeyPressed;

    private static byte cooldownDelay, staminaDeductionDelay, staminaRestorationDelay, staminaDebuffDelay, isEnabledDelay = 0;
    private static boolean staminaDebuffSwitch = false;
    private static byte isEnabledCache = 1;

    private static final Identifier SPRINT_DEBUFF_IDENTIFIER = StaminaDebuff.getSprintDebuffIdentifier();

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

        if (isStaminaIneligible(player)) {
            if (isEnabledDelay == 0) { isEnabledCache = getConfig(ISENABLED); isEnabledDelay = 4; return; }
            isEnabledDelay--;

            if (!staminaDebuffSwitch) { return; } // If Debuff Active..

            if (isEnabledCache == 0) { // & User Disables Mod
                staminaDebuffSwitch = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
                stamina = 100;
            }

            if (player.isCreative() || player.isSpectator()) { // & User Switches Gamemodes
                staminaDebuffSwitch = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
            }

            return;
        }

        restoreStamina(isSprinting, isJumping, isInWater);

        if (staminaDeductionDelay == 0) {
            deductStamina(isSprinting, isJumping);
            staminaDeductionDelay = getConfig(STAMINADEDUCTIONDELAY);
            if (stamina <= 25 && (isSprinting || isJumping) && getConfig(LOWSTAMINAPING) == 1) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2);
            }
        } else { staminaDeductionDelay--; }

        if (staminaDebuffDelay == 0) {
            if (stamina <= 0 && !staminaDebuffSwitch) {
                staminaDebuffSwitch = true;

                activateDebuff();
            }
            if (stamina > 0 && staminaDebuffSwitch) {
                staminaDebuffSwitch = false;

                deactivateDebuff();
            }
            staminaDebuffDelay = 20;
        } else { staminaDebuffDelay--; }

    }

    private static void activateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        ClientPlayNetworking.send(SPRINT_DEBUFF_IDENTIFIER, buf);
    }

    private static void deactivateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        ClientPlayNetworking.send(SPRINT_DEBUFF_IDENTIFIER, buf);
    }

    public static boolean isOutOfStamina() { return staminaDebuffSwitch; } // For Debuff in KeyboardInputMixin

    private static void deductStamina(boolean isSprinting, boolean isJumping) {
        if (!isSprinting && !isJumping) { return; }
        if (stamina == 0 || stamina < 0) { stamina = 0; return; }

        if (isSprinting) { stamina = stamina - getConfig(SPRINTDEDUCTIONAMOUNT); cooldownDelay = getConfig(COOLDOWNDELAY); } // Sprint Deduct

        if (isJumping) { stamina = stamina - getConfig(JUMPDEDUCTIONAMOUNT); cooldownDelay = getConfig(COOLDOWNDELAY); } // Jump Deduct
    }

    private static void restoreStamina(boolean isSprinting, boolean isJumping, boolean isInWater) {
        if (isSprinting || isJumping) { return; }
        if (isInWater) { restoreStaminaAlternate(); return; }
        if (cooldownDelay != 0) { cooldownDelay--; return; } // Cooldown Delay
        if (stamina == 100) { return; }

        if (staminaRestorationDelay != 0) { staminaRestorationDelay--; return; } // Stamina Restoration Delay

        stamina = stamina + getConfig(STAMINARESTORATIONAMOUNT);
        staminaRestorationDelay = getConfig(STAMINARESTORATIONDELAY);
        if (stamina > 100) {
            stamina = 100;
        }
    }

    private static void restoreStaminaAlternate() {
        if (cooldownDelay != 0) { cooldownDelay--; return; } // Cooldown Delay
        if (stamina == 100) { return; }

        if (staminaRestorationDelay != 0) { staminaRestorationDelay--; return; } // Stamina Restoration Delay

        stamina = stamina + getConfig(STAMINARESTORATIONAMOUNT);
        staminaRestorationDelay = (byte) (getConfig(STAMINARESTORATIONDELAY) + 2);
        if (stamina > 100) {
            stamina = 100;
        }
    }

    // Util
    public static boolean isStaminaIneligible(ClientPlayerEntity player) {
        return player.isCreative() || player.isSpectator() || isRidingVehicle(player)
                || getConfig(ISENABLED) == 0 || client.isPaused() || player.isDead();
    }

    private static boolean isRidingVehicle(ClientPlayerEntity player) {
        return player.getVehicle() != null;
    }
}
