package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

import java.util.ArrayList;
import java.util.List;

public class WayStonesListener implements Listener {


    private final PaperWayStones plugin;

    public WayStonesListener(PaperWayStones plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    private void showJavaBook(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player.getWorld().getUID());

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 14) {
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            current_page = current_page.append(Component.text("• " + waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren"))));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

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
            Player player = event.getPlayer();


            Location location = event.getBlock().getLocation();
            if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) {
                event.setCancelled(true);
                return;
            }
/*
            new AnvilGUI.Builder().title("Gebe dem Waystone einen Namen").itemLeft(new ItemStack(Material.STONE_BRICK_WALL)).onComplete((p, name) -> {
                if (name.length() > 16) {
                    return AnvilGUI.Response.text("Maximal 16 Zeichen!");
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PaperWayStones.plugin.createWayStone(player, location, name);
                    }
                }.runTask(PaperWayStones.plugin);
                return AnvilGUI.Response.close();
            }).preventClose().plugin(PaperWayStones.plugin).open(player);
            */

            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null) {
                StoredWaystone waystone = plugin.getStorage().getWaystone(clickedBlock.getLocation().getBlockX(), clickedBlock.getLocation().getBlockY(), clickedBlock.getLocation().getBlockZ(), clickedBlock.getWorld().getUID());
                if (waystone != null) {
                    event.setCancelled(true);
                    if (plugin.floodgateIntegration != null) {
                        boolean bedrock = org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
                        if (bedrock) {
                            //FloodgateIntegration.showBedrockForm(event.getPlayer());
                        }
                    }

                    showJavaBook(event.getPlayer());
                }
            }
        }
    }

}
