package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class WaystonesScreen {

    private PaperWayStones plugin;

    private JavaScreen java;

    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.java = new JavaScreen(plugin);
    }

    public boolean isBedrockPlayer(Player player) {
        if (plugin.floodgateIntegration != null) {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        }
        return false;
    }

    public void list(Player player, String search) {
        if (isBedrockPlayer(player)) {
            //FloodgateIntegration.showBedrockForm(event.getPlayer());
        }
        else {
            java.list(player, search);
        }
    }

    public void menu(Player player, @Nullable StoredWaystone waystone) {
        if (isBedrockPlayer(player)) {
            //FloodgateIntegration.showBedrockForm(event.getPlayer());
        }
        else {
            java.menu(player, waystone);
        }
    }
}
