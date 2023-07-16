package com.kalimero2.team.waystones.paper.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.SerializableWayStone;
import com.kalimero2.team.waystones.paper.SerializableWayStones;
import com.kalimero2.team.waystones.paper.WayStoneDataTypes;
import com.kalimero2.team.waystones.paper.compat.ClaimsIntegration;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WayStonesListener implements Listener {

    private static SerializableWayStones lastWayStones = null;
    private static Book lastBook = null;

    private static void showJavaBook(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
        if (!wayStones.equals(lastWayStones) || lastBook == null) {
            for (Map.Entry<Integer, Location> entry : wayStones.getWayStones().entrySet()) {
                Integer integer = entry.getKey();
                Location location = entry.getValue();
                CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), PaperWayStones.plugin);
                SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
                if (wayStone == null) {
                    PaperWayStones.plugin.getLogger().warning("Removed WayStone with ID " + integer + " it can't be serialized");
                    wayStones.removeWayStone(integer);
                    PaperWayStones.plugin.setSerializableWayStones(player.getWorld(), wayStones);
                }
                counter++;
                if (counter == 14) {
                    pages.add(current_page);
                    current_page = Component.empty();
                    counter = 0;
                }
                current_page = current_page.append(Component.text("• " + wayStone.getName()).clickEvent(ClickEvent.runCommand("/waystone tp " + integer)).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren"))));
                current_page = current_page.append(Component.newline());
            }
            pages.add(current_page);

            lastBook = Book.book(Component.empty(), Component.empty(), pages);
            lastWayStones = wayStones;
        }

        player.openBook(lastBook);
    }

    @EventHandler
    public void onCustomBlockDataMove(CustomBlockDataMoveEvent event) {
        CustomBlockData customBlockData = event.getCustomBlockData();
        if (customBlockData.has(PaperWayStones.WAYSTONE_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        ItemStack waystone = PaperWayStones.plugin.getItem();
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
        if (PaperWayStones.plugin.claimsIntegration) {
            if (ClaimsIntegration.shouldCancel(event.getBlock().getChunk(), event.getPlayer())) {
                return;
            }
        }
        if (event.getBlock().getType() == Material.STONE_BRICK_WALL && event.getItemInHand().isSimilar(PaperWayStones.plugin.getItem())) {
            Player player = event.getPlayer();


            Location location = event.getBlock().getLocation();
            event.setCancelled(true);
            if (!location.clone().add(0, 1, 0).getBlock().isEmpty()) {
                return;
            }
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

            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!event.getPlayer().hasPermission("waystones.remove")) {
            return;
        }
        Block eventBlock = event.getBlock();
        if ((eventBlock.getType() == Material.STONE_BRICK_WALL)) {
            if (eventBlock != null) {
                CustomBlockData customBlockData = new CustomBlockData(eventBlock, PaperWayStones.plugin);
                if (customBlockData.has(PaperWayStones.WAYSTONE_KEY)) {
                    event.setCancelled(true);
                    SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
                    if (lastWayStones == null) {
                        lastWayStones = PaperWayStones.plugin.getSerializableWayStones(event.getPlayer().getWorld());
                    }
                    if (wayStone != null) {
                        int waystoneId = lastWayStones.getWayStone(wayStone.getLocation());
                        event.getPlayer().sendMessage(Component.text("Click to remove").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/waystones remove " + waystoneId)));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        event.getBlocks().forEach(block -> {
            Collection<FallingBlock> nearbyEntitiesByType = block.getLocation().getNearbyEntitiesByType(FallingBlock.class, 2);
            nearbyEntitiesByType.forEach(fallingBlock -> {
                if (fallingBlock.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)) {
                    event.setCancelled(true);
                }
            });
        });
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        event.getBlocks().forEach(block -> {
            Collection<FallingBlock> nearbyEntitiesByType = block.getLocation().getNearbyEntitiesByType(FallingBlock.class, 2);
            nearbyEntitiesByType.forEach(fallingBlock -> {
                if (fallingBlock.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)) {
                    event.setCancelled(true);
                }
            });
        });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().forEach(block -> {
            if (new CustomBlockData(block, PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY)) {
                event.blockList().remove(block);
            }
        });
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && new CustomBlockData(clickedBlock, PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY)) {
                event.setCancelled(true);
                if (PaperWayStones.plugin.floodgateIntegration) {
                    boolean bedrock = org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
                    if (bedrock) {
                        FloodgateIntegration.showBedrockForm(event.getPlayer());
                    }
                }
                showJavaBook(event.getPlayer());
            }
        }
    }

}
