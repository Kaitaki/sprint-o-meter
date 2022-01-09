package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static com.paperscp.sprintometer.SprintOMeter.client;


@Environment(EnvType.CLIENT)
public class ActionStamina {

    public static int Stamina = 100;
    public static boolean multiplayerWarned = false;
    public static boolean isJumpKeyPressed;

    private static int i = 0; // Cooldown Delay
    private static int i2 = 0; // Stamina Deduction Delay
    private static int i3 = 0; // Stamina Restoration Delay
    private static int i4 = 0; // Stamina Debuff Delay
    private static boolean i5 = false; // Stamina Debuff Switch

    // Server Packet Array
    private static Object[] configArray;

    public static void tick() {

        ClientPlayerEntity player = client.player;

        boolean isSprinting = player.isSprinting();
        boolean isJumping = isJumpKeyPressed;

        if (staminaEligible()) {

            staminaRestoration(isSprinting, isJumping);

            if (i2 == 0) {
                staminaDeduction(isSprinting, isJumping); i2 = configurator(2);
                if (Stamina <= 25 && (isSprinting || isJumping)) { if (intToBoolean(configurator(8))) { player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2); } }
            } else { i2--; }

            if (i4 == 0) {
                if (Stamina == 0 && !i5) { i5 = true; }
                if (Stamina != 0 && i5) { i5 = false; } i4 = 20;
            } else { i4--; }
        }
    }

    public static boolean outOfStamina() { return i5; } // For Debuff in KeyboardInputMixin

    private static void staminaDeduction(boolean isSprinting, boolean isJumping) {
        if (isSprinting && Stamina != 0) { Stamina = Stamina - configurator(4); i = configurator(1); }
        else if (Stamina < 0) {Stamina = 0;} // Sprint Deduct

        if (isJumping && Stamina != 0) { Stamina = Stamina - configurator(5); i = configurator(1); }
        else if (Stamina < 0) {Stamina = 0;} // Jump Deduct
    }

    private static void staminaRestoration(boolean isSprinting, boolean isJumping) {
        if (isSprinting || isJumping) { return; }
        if (i != 0) { i--; } else if (Stamina != 100 ) { if (i3 != 0) { i3--; return; } Stamina = Stamina + configurator(6); i3 = configurator(3); }
        if (Stamina > 100) {Stamina = 100;}
    }

    public static boolean staminaEligible() {
        ClientPlayerEntity player = client.player;

        return !(player.isCreative() || player.isSpectator() || isRidingVehicle(player) || client.isPaused() || configurator(7) == 0);
    }

    private static boolean isRidingVehicle(ClientPlayerEntity player) {
        return player.getVehicle() != null;
    }

    // Util
    private static boolean intToBoolean(int intValue) { return intValue != 0; }

    private static int configurator(int ix) {
        boolean is_singleplayer = client.isInSingleplayer();

        if (is_singleplayer) {
            switch (ix) {
                case 1: return SprintOMeterServer.sprintConfig.coolDownDelay;
                case 2: return SprintOMeterServer.sprintConfig.staminaDeductionDelay;
                case 3: return SprintOMeterServer.sprintConfig.staminaRestorationDelay;
                case 4: return SprintOMeterServer.sprintConfig.sprintDeductionAmount;
                case 5: return SprintOMeterServer.sprintConfig.jumpDeductionAmount;
                case 6: return SprintOMeterServer.sprintConfig.staminaRestorationAmount;
                case 7: return SprintOMeterServer.sprintConfig.enableSprintOMeter ? 1 : 0;
                case 8: return SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0;

                default:
                    SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                    throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
            }
        } else {
            if (!multiplayerWarned) { // Multiplayer Notice
                multiplayerWarned = true;
                client.inGameHud.getChatHud().addMessage(new LiteralText("[Sprint O' Meter]: Using server config options..").formatted(Formatting.GRAY));
            }

            switch (ix) {
                case 1: return (int) configArray[0]; // Cooldown Delay - Delay between when the player stops jumping and when it starts restoring
                case 2: return (int) configArray[1]; // Stamina Deduction Delay - How fast the stamina reduces while sprinting (Also controls the frequency of the warning ping)
                case 3: return (int) configArray[2]; // Stamina Restoration Delay - How fast the stamina restores
                case 4: return (int) configArray[3]; // Stamina Sprint Deduction Amount - How much the sprinting deducts
                case 5: return (int) configArray[4]; // Stamina Jump Deduction Amount - How much the jumping deducts
                case 6: return (int) configArray[5]; // Stamina Restoration Amount - How much stamina is restored every tick
                case 7: return (boolean) configArray[6] ? 1 : 0; // Is Enabled? - Determines if SprintOMeter enabled or not
                case 8: return SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0; // Warning Ping Enabled? - Determines if the warning ping should play or not (This 'case' is the same as the singleplayer one as the warning ping is a completely client-sided feature.)

                default:
                    SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                    throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
            }
        }
    }

    public static void packetSetter(Object[] configIntArray) {
        boolean is_singleplayer = client.isInSingleplayer();

        if (!is_singleplayer) {
            configArray = configIntArray;
        }
    }
}
