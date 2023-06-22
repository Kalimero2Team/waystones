package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class WayStonesListener implements Listener {


    private final PaperWayStones plugin;
    private final WaystonesScreen screen;

    public WayStonesListener(PaperWayStones plugin) {
        this.plugin = plugin;
        this.screen = new WaystonesScreen(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }




    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event){
        ItemStack waystone = plugin.getItem();
        if (waystone.isSimilar(event.getInventory().getFirstItem())) {
            event.getInventory().close();
            event.setResult(waystone);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.isCancelled()) {
            return;
        }
        if (plugin.claimsIntegration != null) {
            if (plugin.claimsIntegration.shouldCancel(event.getBlock().getChunk(), event.getPlayer())) {
                return;
            }
        }

        if (event.getBlock().getType() == Material.STONE_BRICK_WALL && event.getItemInHand().isSimilar(plugin.getItem())) {

            Location location = event.getBlock().getLocation();
            if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) {
                event.setCancelled(true);
                return;
            }

            /*
            new AnvilGUI.Builder().title("Gebe dem Waystone einen Namen").itemLeft(plugin.getItem()).onClick((n, state) -> {
                if (state.getText().length() > 16) {
                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Maximal 16 Zeichen!"));
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getStorage().addWaystone(state.getText(), event.getPlayer().getUniqueId(), location.getChunk().getX(), location.getChunk().getZ(), location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
                    }
                }.runTask(plugin);
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            }).preventClose().plugin(plugin).open(event.getPlayer());

            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            }
            event.setCancelled(true);

             */
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        StoredWaystone waystone = plugin.getStorage().getWaystone(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ(), block.getWorld().getUID());
        boolean bottom = false;
        if (waystone == null) {
            Block blockBelow = block.getWorld().getBlockAt(block.getLocation().clone().add(0, -1, 0));
            waystone = plugin.getStorage().getWaystone(blockBelow.getLocation().getBlockX(), blockBelow.getLocation().getBlockY(), blockBelow.getLocation().getBlockZ(), blockBelow.getWorld().getUID());
            bottom = true;
        }
        if (waystone != null) {
            int waystoneID = waystone.id();
            plugin.getStorage().removeWaystone(waystoneID);
            if (bottom) block.getWorld().setType(block.getLocation().clone().add(0, 1, 0), Material.AIR);
            else block.getWorld().setType(block.getLocation().clone().add(0, -1, 0), Material.AIR);
            event.getPlayer().sendMessage(Component.text("Waystone with ID " + waystoneID + " was removed!").color(TextColor.color(180, 0, 0)));
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null) {
                StoredWaystone waystone = plugin.getStorage().getWaystone(clickedBlock.getLocation().getBlockX(), clickedBlock.getLocation().getBlockY(), clickedBlock.getLocation().getBlockZ(), clickedBlock.getWorld().getUID());
                if (waystone == null) {
                    Block blockBelow = clickedBlock.getWorld().getBlockAt(clickedBlock.getLocation().add(0, -1, 0));
                    waystone = plugin.getStorage().getWaystone(blockBelow.getLocation().getBlockX(), blockBelow.getLocation().getBlockY(), blockBelow.getLocation().getBlockZ(), blockBelow.getWorld().getUID());
                }
                if (waystone != null) {
                    event.setCancelled(true);
                    screen.menu(event.getPlayer(), waystone);
                }
            }
        }
    }

}