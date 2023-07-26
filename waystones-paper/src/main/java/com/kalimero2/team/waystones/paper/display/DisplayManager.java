package com.kalimero2.team.waystones.paper.display;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DisplayManager {


    public static NamespacedKey key;


    private Storage storage;
    private PaperWayStones plugin;


    public DisplayManager(PaperWayStones plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        key = new NamespacedKey(plugin, "waystone");
    }


    public void updateDisplay(StoredWaystone waystone) {
        clearDisplay(waystone);

        Location location = waystone.location();
        World world = location.getWorld();

        Location centerLocation = location.toCenterLocation();
        Location topLocation = centerLocation.clone().add(0, 1, 0);
        Block centerLocationBlock = centerLocation.getBlock();
        centerLocationBlock.setType(Material.BARRIER);
        Block topLocationBlock = topLocation.getBlock();
        topLocationBlock.setType(Material.BARRIER);

        ItemDisplay itemDisplay = world.spawn(centerLocation, ItemDisplay.class);
        itemDisplay.setItemStack(plugin.getItem());

        PersistentDataContainer itemDataContainer = itemDisplay.getPersistentDataContainer();
        itemDataContainer.set(key, PersistentDataType.BOOLEAN, true);

        Location textDisplayLocation = topLocation.clone().add(0, 0.75, 0);
        TextDisplay textDisplay = world.spawn(textDisplayLocation, TextDisplay.class);
        textDisplay.text(Component.text(waystone.name()));
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        PersistentDataContainer textDataContainer = textDisplay.getPersistentDataContainer();
        textDataContainer.set(key, PersistentDataType.BOOLEAN, true);
    }
    public void clearDisplay(StoredWaystone waystone) {
        Location location = waystone.location();
        World world = location.getWorld();

        Location centerLocation = location.toCenterLocation();
        Location topLocation = centerLocation.clone().add(0, 1, 0);
        Block centerLocationBlock = centerLocation.getBlock();
        if (centerLocationBlock.getType().equals(Material.BARRIER)) centerLocationBlock.setType(Material.AIR);
        Block topLocationBlock = topLocation.getBlock();
        if (topLocationBlock.getType().equals(Material.BARRIER)) topLocationBlock.setType(Material.AIR);

        Location textDisplayLocation = topLocation.clone().add(0, 0.75, 0);

        for (ItemDisplay display : world.getNearbyEntitiesByType(ItemDisplay.class, centerLocation, 0.3)) {
            if (display.getPersistentDataContainer().has(key)) {
                display.remove();
            }
        }
        for (TextDisplay display : world.getNearbyEntitiesByType(TextDisplay.class, textDisplayLocation, 0.3)) {
            if (display.getPersistentDataContainer().has(key)) {
                display.remove();
            }
        }
    }


    public void removeAll() {
        for (World world : Bukkit.getWorlds()) {
            for (ItemDisplay display : world.getEntitiesByClass(ItemDisplay.class)) {
                if (display.getPersistentDataContainer().has(key)) {
                    display.remove();
                }
            }
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                if (display.getPersistentDataContainer().has(key)) {
                    display.remove();
                }
            }
        }
    }


}
