package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WayStonesListener implements Listener {


    private final PaperWayStones plugin;
    private final WaystoneManager manager;
    private final DisplayManager display;
    private final WaystonesScreen screen;

    public WayStonesListener(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
        this.display = new DisplayManager(plugin);
        this.screen = new WaystonesScreen(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }



    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (event.isCancelled()) {
            return;
        }

        if (plugin.claimsIntegration != null) {
            if (plugin.claimsIntegration.shouldCancel(event.getBlock().getChunk(), event.getPlayer())) {
                return;
            }
        }

        ItemStack stack = event.getItemInHand();
        ItemMeta meta = stack.getItemMeta();

        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "portable"))) event.setCancelled(true);

        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "static"))) {

            event.setCancelled(true);

            Location location = event.getBlock().getLocation();
            if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) {
                return;
            }

            screen.create(player, location, stack);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        StoredWaystone waystone = plugin.getManager().getWaystone(block.getLocation());
        if (waystone == null) {
            Block blockBelow = block.getWorld().getBlockAt(block.getLocation().clone().add(0, -1, 0));
            waystone = plugin.getManager().getWaystone(blockBelow.getLocation());
        }
        if (waystone != null) {
            int waystoneID = waystone.id();
            event.getPlayer().sendMessage(Component.text("Click here to remove the waystone!").clickEvent(ClickEvent.suggestCommand("/waystone remove " + waystoneID)));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            if (event.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "portable"))) {
                screen.menu(event.getPlayer(), null);
            }
        } catch (NullPointerException ignored) {}

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null) {
                StoredWaystone waystone = plugin.getManager().getWaystone(clickedBlock.getLocation());
                if (waystone == null) {
                    Block blockBelow = clickedBlock.getWorld().getBlockAt(clickedBlock.getLocation().add(0, -1, 0));
                    waystone = plugin.getManager().getWaystone(blockBelow.getLocation());
                }
                if (waystone != null) {
                    event.setCancelled(true);
                    screen.menu(event.getPlayer(), waystone);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        manager.removePlayerCache(event.getPlayer());
    }

}