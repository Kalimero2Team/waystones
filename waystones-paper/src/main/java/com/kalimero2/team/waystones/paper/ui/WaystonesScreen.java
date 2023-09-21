package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class WaystonesScreen {

    private final PaperWayStones plugin;
    private final Storage storage;


    private final JavaScreen java;
    private final FloodgateIntegration floodgateIntegration;


    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        this.java = new JavaScreen(plugin);
        floodgateIntegration = plugin.floodgateIntegration;
    }

    public boolean isBedrockPlayer(Player player) {
        if (plugin.floodgateIntegration != null) {
            return org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        }
        return false;
    }

    public void search(Player player) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.menu(player);
        }
        else {
            java.search(player, null);
        }
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
            if (player.isSneaking() && waystone != null) {
                settings(player, waystone);
            }
            else floodgateIntegration.menu(player);
        }
        else {
            java.menu(player, waystone);
        }
    }

    public void settings(Player player, @NotNull StoredWaystone waystone) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.settings(player, waystone);
        }
        else {
            java.settings(player, waystone);
        }
    }

    public void create(Player player, Location location, ItemStack stack) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.create(player, location, stack, LastCreationResult.FIRST_CALL);
        }
        else {
            java.create(player, location, stack);
        }
    }

    public void category(Player player, @NotNull StoredWaystone waystone) {
        if (isBedrockPlayer(player)) {
            floodgateIntegration.setCategory(player, waystone, LastCreationResult.FIRST_CALL);
        }
        else {
            java.categorySelection(player, waystone);
        }
    }
}
