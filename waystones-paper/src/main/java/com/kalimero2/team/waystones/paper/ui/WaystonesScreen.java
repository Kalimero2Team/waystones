package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WaystonesScreen {

    private PaperWayStones plugin;
    private FloodgateIntegration floodgateIntegration;

    private JavaScreen java;

    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.java = new JavaScreen(plugin);
        floodgateIntegration = plugin.floodgateIntegration;
    }

    public boolean isBedrockPlayer(Player player) {
        if (plugin.floodgateIntegration != null) {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        }
        return false;
    }


    

    public void list(Player player, String search) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.list(player, search);
        }
        else {
            java.list(player, search);
        }
    }

    public void menu(Player player, @Nullable StoredWaystone waystone) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.menu(player);
        }
        else {
            java.menu(player, waystone);
        }
    }

    public void settings(Player player, @NotNull StoredWaystone waystone) {
        if (isBedrockPlayer(player)) {
            //FloodgateIntegration.showBedrockForm(event.getPlayer());
        }
        else {
            java.settings(player, waystone);
        }
    }

    public void create(Player player, Location location, ItemStack stack) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.create(player, location, stack, false);
        }
        else {
            java.create(player, location, stack);
        }
    }
}
