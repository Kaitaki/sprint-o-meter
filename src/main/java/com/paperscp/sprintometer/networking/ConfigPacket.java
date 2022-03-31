package com.paperscp.sprintometer.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;

public class ConfigPacket {
    private short i = 0; // ByteArray Increment

    public static final Identifier configValuesIdentifier = new Identifier(MOD_ID,"config-values");

    private final byte[] configValues;
    PacketByteBuf buf = PacketByteBufs.create();

    private final ServerPlayerEntity playerEntity;

    public ConfigPacket(ServerPlayerEntity playerEntity, int amount) {
        this.playerEntity = playerEntity;
        this.configValues = new byte[amount];
    }

    public void addConfig(int configOption) {
        configValues[i] = (byte) configOption;
        i++;

//        configValues[i] = 1;
//        i++;
    }

    public void addConfig(boolean configOption) {
        if (configOption) { configValues[i] = 1; }
        else { configValues[i] = 0; }
        i++;
//        configValues[i] = 2;
//        i++;
    }

    public void sendPacket() {
        buf.writeByteArray(configValues);

        ServerPlayNetworking.send(playerEntity, configValuesIdentifier, buf);
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
