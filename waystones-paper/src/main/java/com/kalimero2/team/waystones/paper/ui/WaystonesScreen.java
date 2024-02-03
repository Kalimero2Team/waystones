package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class WaystonesScreen {

    private final PaperWayStones plugin;


    private final NewScreens newScreens;
    private final JavaScreens java;
    private final FloodgateScreens floodgateScreens;


    public WaystonesScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.java = new JavaScreens(plugin);
        this.newScreens = new NewScreens(plugin);
        this.floodgateScreens = new FloodgateScreens(plugin);
    }


    public void search(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.menu(player);
        } else {
            java.search(player, null);
        }
    }

    public void list(Player player, String search) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.list(player, search, null);
        } else {
            java.list(player, search);
        }
    }

    public void menu(Player player, @Nullable StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.menu(player);
        } else {
            java.menu(player, waystone);
        }
    }

    public void browse(Player player, Category category) {
        if (plugin.isBedrockPlayer(player)) {
            //TODO: Implement Browse feature for Bedrock
        } else {
            java.browse(player, category);
        }
    }

    public void browseCategorySelection(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            //TODO: Implement Browse feature for Bedrock
        } else {
            java.browseCategorySelection(player);
        }
    }

    public void settings(Player player, @NotNull StoredWaystone waystone) {
        newScreens.settings(player, waystone);
    }

    public void setup(Player player, Location location, ItemStack stack) {
        newScreens.setup(player, location, stack);
    }

    public void rename(Player player, StoredWaystone waystone) {
        newScreens.rename(player, waystone);
    }

    public void category(Player player, @NotNull StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.setCategory(player, waystone, LastCreationResult.FIRST_CALL);
        } else {
            java.categorySelection(player, waystone);
        }
    }

    public void accessSettings(Player player, StoredWaystone waystone) {
        newScreens.accessSettings(player, waystone);
    }

    public void addAccess(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.accessAdd(player, waystone, LastCreationResult.FIRST_CALL);
        } else {
            java.addAccess(player, waystone);
        }
    }

    public void removeAccess(Player player, StoredWaystone waystone) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreens.accessRemove(player, waystone);
        } else {
            java.accessSettings(player, waystone);
        }
    }

    public void transferOwnership(Player player, StoredWaystone waystone) {
        newScreens.changeOwner(player, waystone);
    }
}
