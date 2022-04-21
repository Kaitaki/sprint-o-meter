package com.paperscp.sprintometer.config;

import com.paperscp.sprintometer.networking.config.ConfigPacket;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.paperscp.sprintometer.server.SprintOMeterServer.MOD_ID;
import static com.paperscp.sprintometer.server.SprintOMeterServer.sprintConfig;

@Config(name = "sprintometer")
public class SprintOConfig implements ConfigData {

    // Main Options

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean enableSprintOMeter = true;


    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean lowStaminaWarn = true; // Not sent to client (Client Sided Config)

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
    public int maximumStamina = 100;

    // TODO: Find a way to limit the max and min without a slider.

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
    @ConfigEntry.BoundedDiscrete(min = 0, max = 10)
    public int staminaDeductionDelay = 3;

    @ConfigEntry.Category("delayConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int staminaRestorationDelay = 0;

    @ConfigEntry.Category("delayConf")
    @ConfigEntry.Gui.Tooltip(count = 4)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int coolDownDelay = 25;

    // Potion Options

    @ConfigEntry.Category("potionConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart()
    public boolean enablePotions = true; // Not sent to client (Server Sided Config, Sync Needed)

    @ConfigEntry.Category("potionConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.RequiresRestart()
    public int staminaGainPotionDuration = 600; // Not sent to client (Server Sided Config, Sync Needed)

    @ConfigEntry.Category("potionConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 50)
    public int staminaGainBaseAmplifier = 2; // Not sent to client (Server Sided Config)

    @ConfigEntry.Category("potionConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int staminaGainInstantBaseAmplifier = 10; // Not sent to client (Server Sided Config)

    @ConfigEntry.Category("potionConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 25)
    public int staminaGainEffectDelay = 10; // Not sent to client (Server Sided Config)

    // Misc Options

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean verticalSwimDeduct = false;

    @ConfigEntry.Category("misc")
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean deductWithPotionEffect = true;

    //--

    public static void initConfig() {
        AutoConfig.register(SprintOConfig.class, GsonConfigSerializer::new);
        SprintOMeterServer.sprintConfig = AutoConfig.getConfigHolder(SprintOConfig.class).getConfig();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            sendConfigPackets(player);
        });

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;

            sendSyncConfigCheck(player);
        });
    }

    private static void sendConfigPackets(ServerPlayerEntity player) {

        ConfigPacket cp = new ConfigPacket(player, SprintConfigurator.configAmount, false);

        // Feels like there would be a better way but I haven't found it yet
        cp.addConfig(sprintConfig.coolDownDelay); // 0
        cp.addConfig(sprintConfig.staminaDeductionDelay); // 1
        cp.addConfig(sprintConfig.staminaRestorationDelay); // 2
        cp.addConfig(sprintConfig.sprintDeductionAmount);// 3
        cp.addConfig(sprintConfig.jumpDeductionAmount); // 4
        cp.addConfig(sprintConfig.staminaRestorationAmount); // 5
        cp.addConfig(sprintConfig.enableSprintOMeter); // 6
        cp.addConfig(sprintConfig.maximumStamina); // 7
        cp.addConfig(sprintConfig.verticalSwimDeduct); // 8
        cp.addConfig(sprintConfig.deductWithPotionEffect); // 9

        cp.sendPacket();

        if (!player.server.isDedicated()) { return; }

        SprintOMeterServer.logger.info("(SprintOMeter) Server config options sent to " + player.getEntityName() + "!");
    }

    private static void sendSyncConfigCheck(ServerPlayerEntity player) {
        ConfigPacket cp = new ConfigPacket(player, 2, true);

        cp.addConfig(sprintConfig.staminaGainPotionDuration);
        cp.addConfig(sprintConfig.enablePotions);

        cp.sendPacket(new Identifier(MOD_ID, "config-sync-check"));
    }
}

