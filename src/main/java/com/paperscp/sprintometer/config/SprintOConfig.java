package com.paperscp.sprintometer.config;

import com.paperscp.sprintometer.networking.ConfigPacket;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.paperscp.sprintometer.SprintOMeter.client;

@Config(name = "sprintometer")
public class SprintOConfig implements ConfigData {

    // Main Options

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean enableSprintOMeter = true;


    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean lowStaminaWarn = true;

//    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
//    @ConfigEntry.Gui.Tooltip(count = 3)
//    public Difficulty difficulty = Difficulty.Normal;
//
//    enum Difficulty {
//        Easy,
//        Normal,
//        Hard;
//
//        Difficulty() {
//        }
//    }

    // Amount Options

    @ConfigEntry.Category("amountConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int sprintDeductionAmount = 1;

    @ConfigEntry.Category("amountConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int jumpDeductionAmount = 1;

    @ConfigEntry.Category("amountConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int staminaRestorationAmount = 1;

    // Delay Options

    @ConfigEntry.Category("delayConf")
    @ConfigEntry.Gui.Tooltip(count = 4)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int staminaDeductionDelay = 3;

    @ConfigEntry.Category("delayConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int staminaRestorationDelay = 0;

    @ConfigEntry.Category("delayConf")
    @ConfigEntry.Gui.Tooltip(count = 4)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int coolDownDelay = 25;

    //--

    public static void initConfig() {
        AutoConfig.register(SprintOConfig.class, GsonConfigSerializer::new);
        SprintOMeterServer.sprintConfig = AutoConfig.getConfigHolder(SprintOConfig.class).getConfig();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            if (!player.getServer().isSingleplayer()) {
                SprintOMeterServer.logger.info("(SprintOMeter) Sending server config options to " + player.getEntityName() + "..");
                new SprintOConfig().sendConfigPackets(player);
            }
        });
    }

    public void sendConfigPackets(ServerPlayerEntity player) {
        if (player.getEntityWorld().isClient) { return; }

        ConfigPacket cp = new ConfigPacket(player, Configurator.configAmount);

        // Feels like there would be a better way but I haven't found it yet
        cp.addConfig(SprintOMeterServer.sprintConfig.coolDownDelay);
        cp.addConfig(SprintOMeterServer.sprintConfig.staminaDeductionDelay);
        cp.addConfig(SprintOMeterServer.sprintConfig.staminaRestorationDelay);
        cp.addConfig(SprintOMeterServer.sprintConfig.sprintDeductionAmount);
        cp.addConfig(SprintOMeterServer.sprintConfig.jumpDeductionAmount);
        cp.addConfig(SprintOMeterServer.sprintConfig.staminaRestorationAmount);
        cp.addConfig(SprintOMeterServer.sprintConfig.enableSprintOMeter);

        cp.sendPacket();

        SprintOMeterServer.logger.info("(SprintOMeter) Server config options sent!");
    }

    public static class Configurator {
        // Server Packet Array
        private static byte[] configArray;

        public static final short configAmount = 7;

        public static byte getConfig(ConfiguratorOptions configOption) {
            boolean is_singleplayer = client.isInSingleplayer();

            if (is_singleplayer) {
                switch (configOption) {
                    case COOLDOWNDELAY: // Cooldown Delay - Delay between when the player stops jumping and when it starts restoring
                        return (byte) SprintOMeterServer.sprintConfig.coolDownDelay;
                    case STAMINADEDUCTIONDELAY: // Stamina Deduction Delay - How fast the stamina reduces while sprinting (Also controls the frequency of the warning ping)
                        return (byte) SprintOMeterServer.sprintConfig.staminaDeductionDelay;
                    case STAMINARESTORATIONDELAY: // Stamina Restoration Delay - How fast the stamina restores
                        return (byte) SprintOMeterServer.sprintConfig.staminaRestorationDelay;
                    case SPRINTDEDUCTIONAMOUNT: // Stamina Sprint Deduction Amount - How much the sprinting deducts
                        return (byte) SprintOMeterServer.sprintConfig.sprintDeductionAmount;
                    case JUMPDEDUCTIONAMOUNT: // Stamina Jump Deduction Amount - How much the jumping deducts
                        return (byte) SprintOMeterServer.sprintConfig.jumpDeductionAmount;
                    case STAMINARESTORATIONAMOUNT: // Stamina Restoration Amount - How much stamina is restored every tick
                        return (byte) SprintOMeterServer.sprintConfig.staminaRestorationAmount;
                    case ISENABLED: // Is Enabled? - Determines if SprintOMeter enabled or not
                        return (byte) (SprintOMeterServer.sprintConfig.enableSprintOMeter ? 1 : 0);
                    case LOWSTAMINAPING: // Warning Ping Enabled? - Determines if the warning ping should play or not (This 'case' is the same as the singleplayer one as the warning ping is a completely client-sided feature.)
                        return (byte) (SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0);

                    default:
                        SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                        throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
                }
            }
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
                case LOWSTAMINAPING:
                    return (byte) (SprintOMeterServer.sprintConfig.lowStaminaWarn ? 1 : 0);

                default:
                    SprintOMeterServer.logger.fatal("Something happened while trying to get config values!");
                    throw new IndexOutOfBoundsException("Sprint O' Meter Configurator received invalid input!");
            }

        }

        public static void setClientConfig(byte[] configIntArray) {
            boolean is_singleplayer = client.isInSingleplayer();

            if (!is_singleplayer) { configArray = configIntArray; }
        }
    }

}

