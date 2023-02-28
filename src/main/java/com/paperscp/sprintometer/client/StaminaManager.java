package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.config.SprintConfigurator;
import com.paperscp.sprintometer.effects.SprintStatusEffect;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import com.paperscp.sprintometer.server.StaminaDebuff;
import net.combatroll.client.CombatRollClient;
import net.combatroll.client.RollManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
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

    private int cooldownDelay, staminaDeductionDelay, staminaRestorationDelay, staminaDebuffDelay = 0;
    static int staminaAttackDelay = 0;
    private static boolean staminaDebuffSwitch = false;

    private boolean configChecked;
    private int isEnabled, isSaturationEnabled = 1;
    private int deductDelayCalc, prevSaturation; // For stamina deduction based on saturation

    ClientPlayerEntity player;
    private boolean isInWater;
    private boolean isSprinting;
    private boolean isJumping;
    private boolean isSwinging;
    static boolean isSwingingBC;
    static boolean isRolling;


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

    public static void isAttacking(boolean bool) {
        isSwingingBC = bool;
    }
    public static void isRolling(boolean bool) {
        isRolling = bool;
    }

    public void tick() {

        player = client.player;

        isInWater = player.isSubmergedInWater();
        isSprinting = player.isSprinting();
        isJumping = isJumping(isJumpKeyPressed);
        isSwinging = player.handSwinging;

//        System.out.println(isJumping+ " | " + player.input.jumping);

        if (!configChecked && !SprintConfigurator.isConfigNull()) {
            isEnabled = getConfig(ISENABLED);
            isSaturationEnabled = getConfig(ENABLESATURATION);

            configChecked = true;
        }

        if (isStaminaIneligible()) {

//            if (isEnabledDelay == 0) {
//                isEnabledCache = getConfig(ISENABLED);
//                isEnabledDelay = 4;
//                return;
//            } isEnabledDelay--;

            if (!staminaDebuffSwitch) { return; } // If Debuff Active..

//            if (isEnabledCache == 0) { // & User Disables Mod
//                staminaDebuffSwitch = false;
//                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
//                stamina = maxStamina;
//            }

            if (player.isCreative() || player.isSpectator()) { // & User Switches Gamemodes
                staminaDebuffSwitch = false;
                if (player.hasStatusEffect(StatusEffects.SLOWNESS)) { deactivateDebuff(); }
            }

            return;
        }

        restoreStamina();

        if (staminaDeductionDelay == 0) {
            deductStamina();

            if (isSaturationEnabled == 1) {
                float saturation = player.getHungerManager().getSaturationLevel();

                if (saturation != prevSaturation) {
                    staminaDeductionDelay = getConfig(STAMINADEDUCTIONDELAY);

                    deductDelayCalc = Math.round((staminaDeductionDelay * (saturation / (float) getConfig(SATURATIONMOD))) + staminaDeductionDelay);
                    prevSaturation = (int) saturation;
                }

                staminaDeductionDelay = deductDelayCalc;
            } else { staminaDeductionDelay = getConfig(STAMINADEDUCTIONDELAY); }

            if (stamina <= quarterStamina && (isSprinting || isJumping || isSwinging || isSwingingBC || isRolling) && sprintConfig.enableLowStaminaWarn) {
                player.playSound(SoundEvents.BLOCK_SMALL_AMETHYST_BUD_PLACE, 0.2f, 1.5f);
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
            if (isInWater) { return false; }
        }

        if (player.verticalCollision) { jumped = false;}

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
        if (isRidingVehicle()) { return; }
        if (hasStaminaGainEffect()) { return; }
        if (!isSprinting && !isJumping && !isSwinging && !isSwingingBC && !isRolling) { return; }

        if (isSprinting) {
            int sprintDeductAmt = getConfig(SPRINTDEDUCTIONAMOUNT);

            if (sprintDeductAmt != 0) {
                cooldownDelay = getConfig(COOLDOWNDELAY);
            }

            if (stamina == 0 || stamina < 0) { stamina = 0; return; }

            stamina = stamina - sprintDeductAmt;
        } // Sprint Deduct

        if (isJumping && !jumped) {
            int jumpDeductAmt = getConfig(JUMPDEDUCTIONAMOUNT);

            jumped = true;
            if (jumpDeductAmt != 0) {
                cooldownDelay = getConfig(COOLDOWNDELAY);
            }

            if (stamina == 0 || stamina < 0) { stamina = 0; return; }

            stamina = stamina - jumpDeductAmt;
        } // Jump Deduct

        if (isSwinging) {
            int swingDeductAmt = getConfig(SWINGDEDUCTIONAMOUNT);

            if (swingDeductAmt != 0) {
                cooldownDelay = getConfig(COOLDOWNDELAY);
            }

            if (stamina == 0 || stamina < 0) { stamina = 0; return; }

            stamina = stamina - swingDeductAmt;
        } // Swing Deduct

        if (isSwingingBC) {
            int bcswingDeductAmt = getConfig(BCSWINGDEDUCTIONAMOUNT);

            if (bcswingDeductAmt != 0) {
                cooldownDelay = getConfig(COOLDOWNDELAY);
            }

            if (stamina == 0 || stamina < 0) { stamina = 0; isSwingingBC = false; return; }

            stamina = stamina - bcswingDeductAmt;
            isSwingingBC = false;
        } // Better Combat Swing Deduct

        if (isRolling) {
            int rollingDeductAmt = getConfig(ROLLINGDEDUCTIONAMOUNT);

            if (rollingDeductAmt != 0) {
                cooldownDelay = getConfig(COOLDOWNDELAY);
            }

            if (stamina == 0 || stamina < 0) {
                stamina = 0; isRolling = false;
                return;
            }

            stamina = stamina - rollingDeductAmt;
            isRolling = false;
        } // Combat Roll Rolling Deduct

    }

    private void restoreStamina() {
        if (stamina == maxStamina) { return; }

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

        if (staminaRestorationDelay != 0) { staminaRestorationDelay--; return; } // Stamina Restoration Delay

        stamina += getConfig(STAMINARESTORATIONAMOUNT);
        staminaRestorationDelay = getConfig(STAMINARESTORATIONDELAY) + 2;
        if (stamina > maxStamina) {
            stamina = maxStamina;
        }
    }

    // Util
    public boolean isStaminaIneligible() {
        return player.isCreative() || player.isSpectator() ||
                isEnabled == 0 || client.isPaused() || player.isDead();
    }

    private boolean isRidingVehicle() {
        return player.getVehicle() != null && !(player.getVehicle() instanceof BoatEntity);
    }

    private boolean hasStaminaGainEffect() {
        if (getConfig(DEDUCTWITHPOTIONEFFECT) == 1) { return false; }

        Map<StatusEffect, StatusEffectInstance> m = player.getActiveStatusEffects();

        return m.containsKey(SprintStatusEffect.STAMINA_GAIN) || m.containsKey(SprintStatusEffect.STAMINA_GAIN_INSTANT);
    }
}
