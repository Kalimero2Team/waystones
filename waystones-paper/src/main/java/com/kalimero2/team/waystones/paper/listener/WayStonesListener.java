package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class WayStonesListener implements Listener {


    private final PaperWayStones plugin;
    private final Storage storage;
    private final DisplayManager display;
    private final WaystonesScreen screen;

    public WayStonesListener(PaperWayStones plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        this.display = new DisplayManager(plugin);
        this.screen = new WaystonesScreen(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }



    @EventHandler()
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

        String name = null;

        ItemStack stack = event.getItemInHand();
        ItemMeta meta = stack.getItemMeta();
        if (!meta.getDisplayName().equals(plugin.getItem().getItemMeta().getDisplayName())) {
            name = meta.getDisplayName();
        }

        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "portable"))) event.setCancelled(true);

        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "static"))) {

            event.setCancelled(true);

            Location location = event.getBlock().getLocation();
            if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) {
                return;
            }

            if (name == null) {
                player.sendMessage(Component.text("Das Anvil GUI ist aktuell noch nicht implementiert"));
                return;
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
                 */
            }

            if (!storage.nameFree(name)) {
                player.sendMessage(Component.text("Dieser Name ist bereits vergeben!").color(TextColor.color(255, 73, 0)));
                return;
            }

            storage.addWaystone(name, player.getUniqueId(), 0, location.getChunk().getX(), location.getChunk().getZ(), location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
            StoredWaystone waystone = storage.getWaystone(location.getBlockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
            stack.setAmount(stack.getAmount() - 1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    display.updateDisplay(waystone);
                }
            }.runTaskLater(plugin, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        StoredWaystone waystone = plugin.getStorage().getWaystone(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ(), block.getWorld().getUID());
        if (waystone == null) {
            Block blockBelow = block.getWorld().getBlockAt(block.getLocation().clone().add(0, -1, 0));
            waystone = plugin.getStorage().getWaystone(blockBelow.getLocation().getBlockX(), blockBelow.getLocation().getBlockY(), blockBelow.getLocation().getBlockZ(), blockBelow.getWorld().getUID());
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