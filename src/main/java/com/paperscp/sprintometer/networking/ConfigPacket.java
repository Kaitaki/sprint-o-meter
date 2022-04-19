package com.paperscp.sprintometer.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class ConfigPacket {
    private int arrayPos = 0;

    public static final Identifier CONFIG_VALUES_IDENTIFIER = new Identifier(MOD_ID,"config-values");

    private final int[] configValues;
    private final PacketByteBuf buf = PacketByteBufs.create();

    private final ServerPlayerEntity playerEntity;

    public ConfigPacket(ServerPlayerEntity playerEntity, int amount, boolean includeLength) {
        this.playerEntity = playerEntity;

        if (includeLength) {
            this.configValues = new int[amount + 1];

            this.configValues[0] = configValues.length - 1;
            arrayPos++;

            return;
        }

        this.configValues = new int[amount];
    }

    public void addConfig(int configOption) {
        configValues[arrayPos] = configOption;
        arrayPos++;

//        configValues[i] = 1;
//        i++;
    }

    public void addConfig(boolean configOption) {
        if (configOption) { configValues[arrayPos] = 1; }
        else { configValues[arrayPos] = 0; }
        arrayPos++;
//        configValues[i] = 2;
//        i++;
    }

    public void sendPacket() {
        buf.writeIntArray(configValues);

        ServerPlayNetworking.send(playerEntity, CONFIG_VALUES_IDENTIFIER, buf);
    }

    public void sendPacket(Identifier customIdentifier) {
        buf.writeIntArray(configValues);

        ServerPlayNetworking.send(playerEntity, customIdentifier, buf);
    }

//    public static byte[] decodePacket(byte[] configValues, int configAmount) {
//        boolean valueNumber = true;
//        short inc = 0;
//        byte[] returnArray = new byte[configAmount];
//
//        for (short i = 0; i < configValues.length; i++) {
//            if (valueNumber) {
//                if (configValues[i + 1] == 1) {
//                    returnArray[inc] = configValues[i];
//                    inc++;
//                }
//                else if (configValues[i + 1] == 2) {
//                    returnArray[inc] = configValues[i];
//                    inc++;
//                }
//
//                valueNumber = false;
//            } else {
//                valueNumber = true;
//            }
//        }
//
//        return returnArray;
//    }

}
