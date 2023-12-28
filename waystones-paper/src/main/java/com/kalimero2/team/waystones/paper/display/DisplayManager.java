package com.kalimero2.team.waystones.paper.display;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DisplayManager {
    public final NamespacedKey WAYSTONE_KEY;

    private final WaystoneManager manager;
    private final PaperWayStones plugin;


    public DisplayManager(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
        WAYSTONE_KEY = new NamespacedKey(plugin, "waystone");
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
        itemDisplay.setItemStack(plugin.getStatic());

        PersistentDataContainer itemDataContainer = itemDisplay.getPersistentDataContainer();
        itemDataContainer.set(WAYSTONE_KEY, PersistentDataType.BOOLEAN, true);

        Location textDisplayLocation = topLocation.clone().add(0, 0.75, 0);
        TextDisplay textDisplay = world.spawn(textDisplayLocation, TextDisplay.class);
        textDisplay.text(Component.text(waystone.name()));
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        PersistentDataContainer textDataContainer = textDisplay.getPersistentDataContainer();
        textDataContainer.set(WAYSTONE_KEY, PersistentDataType.BOOLEAN, true);
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
            if (display.getPersistentDataContainer().has(WAYSTONE_KEY)) {
                display.remove();
            }
        }
        for (TextDisplay display : world.getNearbyEntitiesByType(TextDisplay.class, textDisplayLocation, 0.3)) {
            if (display.getPersistentDataContainer().has(WAYSTONE_KEY)) {
                display.remove();
            }
        }
    }


    public void removeAll() {
        for (World world : plugin.getServer().getWorlds()) {
            for (ItemDisplay display : world.getEntitiesByClass(ItemDisplay.class)) {
                if (display.getPersistentDataContainer().has(WAYSTONE_KEY)) {
                    display.remove();
                }
            }
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                if (display.getPersistentDataContainer().has(WAYSTONE_KEY)) {
                    display.remove();
                }
            }
        }
    }


    public void updateAll() {

        int batchSize = 5;

        removeAll();

        List<StoredWaystone> waystones = manager.getWaystones().stream().toList();

        plugin.getLogger().info("Updating " + waystones.size() + " waystones with a batch size of " + batchSize + ".. This will take " + Math.ceil(waystones.size() / batchSize) + " ticks");

        for (int i = 0; i <= waystones.size(); i++) {

            if (i == waystones.size()) {
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getLogger().info("Finished updating all Waystone displays.");
                    }
                };

                runnable.runTaskLater(plugin, Math.floorDiv(i, batchSize)+1);
            }

            else {
                StoredWaystone waystone = waystones.get(i);
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateDisplay(waystone);
                    }
                };

                runnable.runTaskLater(plugin, Math.floorDiv(i, batchSize));
            }

        }
    }


}
