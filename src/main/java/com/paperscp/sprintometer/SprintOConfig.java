package com.paperscp.sprintometer;

import com.paperscp.sprintometer.networking.ConfigPacket;
import com.paperscp.sprintometer.server.SprintOMeterServer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

@Config(name = "sprintometer")
public class SprintOConfig implements ConfigData {

    // Main Options

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean enableSprintOMeter = true;


    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean lowStaminaWarn = true;

    // Amount Options

    @ConfigEntry.Category("amountConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int sprintDeductionAmount = 1;

    @ConfigEntry.Category("amountConf")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
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

    public static void configInit() {
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

        ConfigPacket cp = new ConfigPacket(player, 7);

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
}
