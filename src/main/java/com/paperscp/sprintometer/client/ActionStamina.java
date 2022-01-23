package com.paperscp.sprintometer.client;

import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.SprintOMeter.client;


@Environment(EnvType.CLIENT)
public class ActionStamina {

    public static int Stamina = 100;
    public static boolean isJumpKeyPressed;

    private static byte i = 0; // Cooldown Delay
    private static byte i2 = 0; // Stamina Deduction Delay
    private static byte i3 = 0; // Stamina Restoration Delay
    private static byte i4 = 0; // Stamina Debuff Delay
    private static boolean i5 = false; // Stamina Debuff Switch

    private static Identifier sprintDebuffIdentifier = new Identifier("sprintometer", "sprintable");

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
                if (Stamina <= 25 && (isSprinting || isJumping) && intToBoolean(configurator(8))) { player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 2); }
            } else { i2--; }

            if (i4 == 0) {
                if (Stamina <= 0 && !i5) {
                    i5 = true;

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(true);

                    ClientPlayNetworking.send(sprintDebuffIdentifier, buf);
                }
                if (Stamina > 0 && i5) {
                    i5 = false;

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(false);

                    ClientPlayNetworking.send(sprintDebuffIdentifier, buf);
                } i4 = 20;
            } else { i4--; }
        } else if (i5 && configurator(7) == 0) {i5 = false; Stamina = 100;}
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

    private static byte configurator(int ix) {
        boolean is_singleplayer = client.isInSingleplayer();

        if (is_singleplayer) {
            switch (ix) {
                case 1: return (byte) SprintOMeterServer.sprintConfig.coolDownDelay;
                case 2: return (byte) SprintOMeterServer.sprintConfig.staminaDeductionDelay;
                case 3: return (byte) SprintOMeterServer.sprintConfig.staminaRestorationDelay;
                case 4: return (byte) SprintOMeterServer.sprintConfig.sprintDeductionAmount;
                case 5: return (byte) SprintOMeterServer.sprintConfig.jumpDeductionAmount;
                case 6: return (byte) SprintOMeterServer.sprintConfig.staminaRestorationAmount;
                case 7: return (byte) (SprintOMeterServer.sprintConfig.enableSprintOMeter ? 1 : 0);
                case 8: return (byte) (SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0);

                default:
                    SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                    throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
            }
        } else {
            switch (ix) {
                case 1: return (byte) configArray[0]; // Cooldown Delay - Delay between when the player stops jumping and when it starts restoring
                case 2: return (byte) configArray[1]; // Stamina Deduction Delay - How fast the stamina reduces while sprinting (Also controls the frequency of the warning ping)
                case 3: return (byte) configArray[2]; // Stamina Restoration Delay - How fast the stamina restores
                case 4: return (byte) configArray[3]; // Stamina Sprint Deduction Amount - How much the sprinting deducts
                case 5: return (byte) configArray[4]; // Stamina Jump Deduction Amount - How much the jumping deducts
                case 6: return (byte) configArray[5]; // Stamina Restoration Amount - How much stamina is restored every tick
                case 7: return (byte) ((boolean) configArray[6] ? 1 : 0); // Is Enabled? - Determines if SprintOMeter enabled or not
                case 8: return (byte) (SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0); // Warning Ping Enabled? - Determines if the warning ping should play or not (This 'case' is the same as the singleplayer one as the warning ping is a completely client-sided feature.)

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
