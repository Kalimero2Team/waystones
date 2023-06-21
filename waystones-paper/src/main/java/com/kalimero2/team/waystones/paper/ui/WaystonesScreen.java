package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.entity.Player;

public class WaystonesScreen {

    private PaperWayStones plugin;

    private JavaScreen java;

    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
    }

    public boolean isBedrockPlayer(Player player) {
        if (plugin.floodgateIntegration != null) {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        }
        return false;
    }

    public void list(Player player) {
        if (isBedrockPlayer(player)) {
            //FloodgateIntegration.showBedrockForm(event.getPlayer());
        }
        else {
            java.list(player);
        }
    }

    public void menu(Player player) {
        if (isBedrockPlayer(player)) {
            //FloodgateIntegration.showBedrockForm(event.getPlayer());
        }
        else {
            java.menu(player);
        }
    }
}
