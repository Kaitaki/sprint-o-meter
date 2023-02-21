package com.paperscp.sprintometer.config;

import com.paperscp.sprintometer.SprintOMeter;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;

import static com.paperscp.sprintometer.SprintOMeter.client;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

@Environment(EnvType.CLIENT)
public class SprintConfigurator {
        // Server Packet Array
        private static int[] configArray;

        public static final short configAmount = 14;

        public static boolean isConfigNull() {
            return configArray == null;
        }

        public static int getConfig(ConfiguratorOptions configOption) {
            if (configArray == null) {return 0;} // Failsafe, 0 until config is loaded/sent

            switch (configOption) {
                case COOLDOWNDELAY:
                    return configArray[0];
                case STAMINADEDUCTIONDELAY:
                    return configArray[1];
                case STAMINARESTORATIONDELAY:
                    return configArray[2];
                case SPRINTDEDUCTIONAMOUNT:
                    return configArray[3];
                case JUMPDEDUCTIONAMOUNT:
                    return configArray[4];
                case STAMINARESTORATIONAMOUNT:
                    return configArray[5];
                case ISENABLED:
                    return configArray[6];
                case MAXSTAMINA:
                    return configArray[7];
                case VERTICALSWIMDEDUCT:
                    return configArray[8];
                case DEDUCTWITHPOTIONEFFECT:
                    return configArray[9];
                case JUMPDEBUFF:
                    return configArray[10];
                case ENABLESATURATION:
                    return configArray[11];
                case SATURATIONMOD:
                    return configArray[12];
                case SWINGDEDUCTIONAMOUNT:
                    return configArray[13];

                default:
                    SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                    throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
            }

        }

        public static void setClientConfig(int[] configIntArray) {
            configArray = configIntArray;

            if (!client.isInSingleplayer()) {
                SprintOMeterServer.logger.info("[Sprint O' Meter]: Switching to server config options..");
            }

            // Update semi-final values in StaminaManager && StaminaHudManager
            SprintOMeter.staminaManager.refreshStamina();
            SprintOMeter.staminaRenderer.staminaHudManager.refreshMaxStamina();
        }

        public static void checkClientConfig(int[] serverConfigArray) {
            System.out.println(Arrays.toString(serverConfigArray));

            ArrayList<String> unsyncedConfig = new ArrayList<>();

            for (int i = 1; i <= serverConfigArray[0]; i++) {
                switch (i) {
                    case 1:
                        if (sprintConfig.staminaGainPotionDuration != serverConfigArray[i]) { unsyncedConfig.add("Potion Duration"); }
                        break;
                    case 2:
                        if ((sprintConfig.enablePotions ? 1 : 0) != serverConfigArray[i]) { unsyncedConfig.add("Enable Potions"); }
                        break;
                }
            }

            if (!unsyncedConfig.isEmpty()) {

                client.inGameHud.getChatHud().addMessage(Text.literal(
                        "[Sprint O' Meter]: Sprint O' Meter can't automatically change the following config value(s) to match the server, ")
                        .formatted(Formatting.YELLOW)
                            .append(Text.literal(Arrays.toString(unsyncedConfig.toArray()))
                                    .formatted(Formatting.GREEN)
                ));

                client.inGameHud.getChatHud().addMessage(
                        Text.literal("Please make sure the value(s) match the server. Unexpected things may occur if not fixed.")
                                .formatted(Formatting.LIGHT_PURPLE)
                );

            }
        }
}
