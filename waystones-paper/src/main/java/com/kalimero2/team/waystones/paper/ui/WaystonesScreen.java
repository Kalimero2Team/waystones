package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WaystonesScreen {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;


    private final JavaScreens java;
    private final FloodgateScreens floodgateIntegration;


    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
        this.java = new JavaScreens(plugin);
        floodgateIntegration = plugin.floodgateIntegration;
    }


    public void search(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.menu(player);
        }
        else {
            java.search(player, null);
        }
    }

    public void list(Player player, String search) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.list(player, search);
        }
        else {
            java.list(player, search);
        }
    }

    public void menu(Player player, @Nullable StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.menu(player);
        }
        else {
            java.menu(player, waystone);
        }
    }

    public void settings(Player player, @NotNull StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.settings(player, waystone);
        }
        else {
            java.settings(player, waystone);
        }
    }

    public void create(Player player, Location location, ItemStack stack) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.create(player, location, stack, LastCreationResult.FIRST_CALL);
        }
        else {
            java.create(player, location, stack);
        }
    }

    public void rename(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.rename(player, waystone, LastCreationResult.FIRST_CALL);
        }
        else {
            java.rename(player, waystone);
        }
    }

    public void category(Player player, @NotNull StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.setCategory(player, waystone, LastCreationResult.FIRST_CALL);
        }
        else {
            java.categorySelection(player, waystone);
        }
    }

    public void accessSettings(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.accessSettings(player, waystone);
        }
        else {
            java.accessSettings(player, waystone);
        }
    }
    public void addAccess(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.accessAdd(player, waystone, LastCreationResult.FIRST_CALL);
        }
        else {
            java.addAccess(player, waystone);
        }
    }

    public void transferOwnership(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateIntegration.settings(player, waystone);
        }
        else {
            java.setOwner(player, waystone);
        }
    }
}
