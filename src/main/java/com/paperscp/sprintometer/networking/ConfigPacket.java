package com.paperscp.sprintometer.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class ConfigPacket {
    private int i = 0; // ByteArray Increment

    public static Identifier configValuesIdentifier = new Identifier(MOD_ID,"config-values");

    private final int [] configValues;
    PacketByteBuf buf = PacketByteBufs.create();

    private final ServerPlayerEntity playerEntity;

    public ConfigPacket(ServerPlayerEntity playerEntity, int amount) {
        this.playerEntity = playerEntity;
        this.configValues = new int[amount * 2];
    }

    public void addConfig(int configOption) {
        configValues[i] = configOption;
        i++;

        configValues[i] = 1;
        i++;
    }

    public void addConfig(boolean configOption) {
        if (configOption) { configValues[i] = 1; i++; configValues[i] = 2; i++; return; }
        configValues[i] = 0;
        i++;

        configValues[i] = 2;
        i++;
    }


    public void sendPacket() {
        buf.writeIntArray(configValues);

        ServerPlayNetworking.send(playerEntity, configValuesIdentifier, buf);
    }

    private static boolean intToBoolean(int intValue) {
        return intValue != 0;
    }

    public static Object[] decodePacket(int[] configValues, int configAmount) {
        boolean valueNumber = true;
        int inc = 0;
        Object[] returnArray = new Object[configAmount];

        for (int i = 0; i < configValues.length; i++) {
            if (valueNumber) {
                if (configValues[i + 1] == 1) {
                    returnArray[inc] = configValues[i];
                    inc++;
                }
                else if (configValues[i + 1] == 2) {
                    returnArray[inc] = intToBoolean(configValues[i]);
                    inc++;
                }

                valueNumber = false;
            } else {
                valueNumber = true;
            }
        }

        return returnArray;
    }

}
