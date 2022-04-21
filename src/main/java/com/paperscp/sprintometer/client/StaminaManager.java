package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.effects.SprintStatusEffect;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import com.paperscp.sprintometer.server.StaminaDebuff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

import static com.paperscp.sprintometer.SprintOMeter.client;
import static com.paperscp.sprintometer.config.ConfiguratorOptions.*;
import static com.paperscp.sprintometer.config.SprintConfigurator.getConfig;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

@Environment(EnvType.CLIENT)
public class StaminaManager {

    private int maxStamina = SprintOMeterServer.sprintConfig.maximumStamina;
    private int stamina = maxStamina;
    private int quarterStamina = (int) Math.round(maxStamina * 0.25);

    public static boolean isJumpKeyPressed = false;
    private boolean jumped = false; // To make sure that stamina only gets deducted when the player jumps off the floor

    private int cooldownDelay, staminaDeductionDelay, staminaRestorationDelay, staminaDebuffDelay, isEnabledDelay = 0;
    private static boolean staminaDebuffSwitch = false;

    private int isEnabledCache = 1;

    ClientPlayerEntity player;
    private boolean isInWater;
    private boolean isSprinting;
    private boolean isJumping;

    private final Identifier SPRINT_DEBUFF_IDENTIFIER = StaminaDebuff.getSprintDebuffIdentifier();

//    public  byte temp(int ix) { // For Debug Menu in StaminaRenderer
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

    public void tick() {

        player = client.player;

        isInWater = player.isSubmergedInWater();
        isSprinting = player.isSprinting();
        isJumping = isJumping(isJumpKeyPressed);

//        System.out.println(isJumping+ " | " + player.input.jumping);

        if (isStaminaIneligible()) {

            if (isEnabledDelay == 0) { isEnabledCache = getConfig(ISENABLED); isEnabledDelay = 4; return; }
            isEnabledDelay--;

            if (!staminaDebuffSwitch) { return; } // If Debuff Active..

            if (isEnabledCache == 0) { // & User Disables Mod
                staminaDebuffSwitch = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
                stamina = maxStamina;
            }

            if (player.isCreative() || player.isSpectator()) { // & User Switches Gamemodes
                staminaDebuffSwitch = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
            }

            return;
        }

        restoreStamina();

        if (staminaDeductionDelay == 0) {
            deductStamina();
            staminaDeductionDelay = getConfig(STAMINADEDUCTIONDELAY);
            if (stamina <= quarterStamina && (isSprinting || isJumping) && sprintConfig.lowStaminaWarn) {
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

        if (stamina > maxStamina) { // Overflow check for StatusEffect sometimes going over max
            stamina = maxStamina;
        }
    }

    private boolean isJumping(boolean isJumpingAccurate) {
        if (getConfig(VERTICALSWIMDEDUCT) == 0) {
            if (isInWater) {return false;}
        }

        if (player.verticalCollision) {jumped = false;}

        return isJumpingAccurate && player.fallDistance == 0.0;
    }

    private void activateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        ClientPlayNetworking.send(SPRINT_DEBUFF_IDENTIFIER, buf);
    }

    private void deactivateDebuff() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        ClientPlayNetworking.send(SPRINT_DEBUFF_IDENTIFIER, buf);
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void applyStatusEffect(int id, int amplifier) {
        switch (id) {
            case 1: // Stamina+
                if (stamina >= maxStamina) { return; }
                stamina += Math.abs(amplifier); // Abs needed just in case integer overflow happens

                break;
            case 2: // Stamina- TODO: Add stamina subtract potions
                break;
        }
    }

    public static boolean isOutOfStamina() { return staminaDebuffSwitch; } // For Debuff in KeyboardInputMixin

    public void refreshStamina() {
        maxStamina = getConfig(MAXSTAMINA);
        quarterStamina = (int) Math.round(maxStamina * 0.25);
    }

    private void deductStamina() {
        if (stamina == 0 || stamina < 0) { stamina = 0; return; }

        if (isRidingVehicle()) { return; }
        if (hasStaminaGainEffect()) { return; }
        if (!isSprinting && !isJumping) { return; }

        if (isSprinting) { stamina = stamina - getConfig(SPRINTDEDUCTIONAMOUNT); cooldownDelay = getConfig(COOLDOWNDELAY); } // Sprint Deduct

        if (isJumping && !jumped) { stamina = stamina - getConfig(JUMPDEDUCTIONAMOUNT); cooldownDelay = getConfig(COOLDOWNDELAY); jumped = true;} // Jump Deduct

    }

    private void restoreStamina() {
        if (stamina == maxStamina) { return; }

        if (isSprinting || isJumping) { return; }
        if (isInWater) { restoreStaminaAlternate(); return; }
        if (cooldownDelay != 0) { cooldownDelay--; return; } // Cooldown Delay

        if (staminaRestorationDelay != 0) { staminaRestorationDelay--; return; } // Stamina Restoration Delay

        stamina += getConfig(STAMINARESTORATIONAMOUNT);
        staminaRestorationDelay = getConfig(STAMINARESTORATIONDELAY);
        if (stamina > maxStamina) {
            stamina = maxStamina;
        }
    }

    private void restoreStaminaAlternate() {
        if (cooldownDelay != 0) { cooldownDelay--; return; } // Cooldown Delay
        if (stamina == maxStamina) { return; }

        if (staminaRestorationDelay != 0) { staminaRestorationDelay--; return; } // Stamina Restoration Delay

        stamina += getConfig(STAMINARESTORATIONAMOUNT);
        staminaRestorationDelay = (byte) (getConfig(STAMINARESTORATIONDELAY) + 2);
        if (stamina > maxStamina) {
            stamina = maxStamina;
        }
    }

    // Util
    public boolean isStaminaIneligible() {
        return player.isCreative() || player.isSpectator() ||
                getConfig(ISENABLED) == 0 || client.isPaused() || player.isDead();
    }

    private boolean isRidingVehicle() {
        return player.getVehicle() != null && !(player.getVehicle() instanceof BoatEntity);
    }

    private boolean hasStaminaGainEffect() {
        if (getConfig(DEDUCTWITHPOTIONEFFECT) == 1) { return false; }

        Map<StatusEffect, StatusEffectInstance> hm = player.getActiveStatusEffects();

        return hm.containsKey(SprintStatusEffect.STAMINA_GAIN) || hm.containsKey(SprintStatusEffect.STAMINA_GAIN_INSTANT);
    }
}
