package com.kalimero2.team.waystones.paper.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent;
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent;
import com.kalimero2.team.waystones.paper.compat.ClaimsIntegration;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.SerializableWayStone;
import com.kalimero2.team.waystones.paper.SerializableWayStones;
import com.kalimero2.team.waystones.paper.WayStoneDataTypes;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WayStonesListener implements Listener {

    @EventHandler
    public void onCustomBlockDataMove(CustomBlockDataMoveEvent event){
        CustomBlockData customBlockData = event.getCustomBlockData();
        if(customBlockData.has(PaperWayStones.WAYSTONE_KEY)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCustomBlockDataRemove(CustomBlockDataRemoveEvent event){
        CustomBlockData customBlockData = event.getCustomBlockData();
        if(customBlockData.has(PaperWayStones.WAYSTONE_KEY)){
            Block block = event.getBlock();
            PaperWayStones.plugin.removeWaystone(block.getLocation());
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.isCancelled()){
            return;
        }
        if(PaperWayStones.plugin.claimsIntegration){
            if(ClaimsIntegration.shouldCancel(event.getBlock().getChunk(), event.getPlayer())){
                return;
            }
        }
        if(event.getBlock().getType() == Material.STONE_BRICK_WALL && event.getItemInHand().isSimilar(PaperWayStones.plugin.getItem())){
            Player player = event.getPlayer();


            Location location = event.getBlock().getLocation();
            if(!location.clone().add(0,1,0).getBlock().isEmpty()){
                event.setCancelled(true);
                return;
            }

            new AnvilGUI.Builder().title("Gebe dem Waystone einen Namen").itemLeft(new ItemStack(Material.STONE_BRICK_WALL)).onComplete((p, name) -> {
                if(name.length() > 16){
                    return AnvilGUI.Response.text("Maximal 16 Zeichen!");
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        PaperWayStones.plugin.createWayStone(player, location,name);
                    }
                }.runTask(PaperWayStones.plugin);
                return AnvilGUI.Response.close();
            }).preventClose().plugin(PaperWayStones.plugin).open(player);

            if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
                event.getItemInHand().setAmount(event.getItemInHand().getAmount()-1);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event){
        event.getBlocks().forEach(block -> {
            Collection<FallingBlock> nearbyEntitiesByType = block.getLocation().getNearbyEntitiesByType(FallingBlock.class, 2);
            nearbyEntitiesByType.forEach(fallingBlock -> {
                if(fallingBlock.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
                    event.setCancelled(true);
                }
            });
        });
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event){
        event.getBlocks().forEach(block -> {
            Collection<FallingBlock> nearbyEntitiesByType = block.getLocation().getNearbyEntitiesByType(FallingBlock.class, 2);
            nearbyEntitiesByType.forEach(fallingBlock -> {
                if(fallingBlock.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
                    event.setCancelled(true);
                }
            });
        });
    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event){
        if(event.getEntity().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        event.blockList().forEach(block -> {
            if(new CustomBlockData(block, PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY)){
                event.blockList().remove(block);
            }
        });
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamage(BlockDamageEvent event){
        if(event.isCancelled()){
            return;
        }
        CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), PaperWayStones.plugin);
        if(customBlockData.has(PaperWayStones.WAYSTONE_KEY)){
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*10, 5,false,false,false));
        }
    }

    @EventHandler
    public void onBlockDamageAbort(BlockDamageAbortEvent event){
        CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), PaperWayStones.plugin);
        if(customBlockData.has(PaperWayStones.WAYSTONE_KEY)){
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()){
            return;
        }

        CustomBlockData customBlockData = new CustomBlockData(event.getBlock(), PaperWayStones.plugin);
        if(customBlockData.has(PaperWayStones.WAYSTONE_KEY)){
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Block clickedBlock = event.getClickedBlock();
            if(clickedBlock != null && new CustomBlockData(clickedBlock, PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY)){
                event.setCancelled(true);
                if(PaperWayStones.plugin.bedrockIntegration){
                    boolean bedrock =  org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
                    if(bedrock){
                        FloodgateIntegration.showBedrockForm(event.getPlayer());
                    }
                }
                showJavaBook(event.getPlayer());
            }
        }
    }

    private static void showJavaBook(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        // Determines the sorting mode for the given player (NOT implemented yet!)
        // 0 = Numerical
        // 1 = Alphabetical
        // 2 = Sorted by popularity (how often players teleport to a specific waystone)
        Integer mode = player.getPersistentDataContainer().get(new NamespacedKey("waystones", "sorting_mode"), PersistentDataType.INTEGER);

        if (mode == null) {
            player.getPersistentDataContainer().set(new NamespacedKey("waystones", "sorting_mode"), PersistentDataType.INTEGER, 0);
            mode = 0;
        }

        // TODO: Implement system to sort entries

        SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
        for (Map.Entry<Integer, Location> entry : wayStones.getWayStones().entrySet()) {
            Integer integer = entry.getKey();
            Location location = entry.getValue();
            CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), PaperWayStones.plugin);
            SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
            counter++;

            if(counter == 13){
                Bukkit.getLogger().info("Counter is 13");
                TextColor color = TextColor.color(0, 0, 0);
                TextColor colorSelected = TextColor.color(0, 150, 255);

                if (mode == 1) current_page.append(Component.text("  [A-Z]").color(colorSelected));
                else current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 1")));

                if (mode == 0) { current_page.append(Component.text("  [1-2]").color(colorSelected)); Bukkit.getLogger().info("success"); }
                else { current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0"))); Bukkit.getLogger().info("Also success"); }

                if (mode == 2) current_page.append(Component.text("  [★★★]").color(colorSelected));
                else current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));

                if (mode < 0 || mode > 2) Bukkit.getLogger().info("Mode is invalid: " + mode);

                Bukkit.getLogger().info("Current Page: "+current_page);

                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            else {
                String action = "add";
                TextColor color = TextColor.color(0, 0, 0);
                if (player.getPersistentDataContainer().has(new NamespacedKey("waystones", "favourite_waystones"))) {
                    for (int id : player.getPersistentDataContainer().get(new NamespacedKey("waystones", "favourite_waystones"), PersistentDataType.INTEGER_ARRAY)) {
                        if (id == integer) {
                            action = "remove";
                            color = TextColor.color(200, 200, 0);
                        }
                    }
                }
                current_page = current_page.append(Component.text("[★]").color(color).clickEvent(ClickEvent.runCommand("/waystone " + action + "favourite " + integer)));
                current_page = current_page.append(Component.text(" " + wayStone.getName()).clickEvent(ClickEvent.runCommand("/waystone tp " + integer)).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren"))));
                current_page = current_page.append(Component.newline());
            }
        }

        if (counter < 13) {

            Bukkit.getLogger().info("Counter is too small :" + counter);

            for (int i = 0; i < 13-counter; i++) {
                current_page.append(Component.newline());
            }

            TextColor color = TextColor.color(0, 0, 0);
            TextColor colorSelected = TextColor.color(0, 150, 255);

            if (mode == 1) current_page.append(Component.text("  [A-Z]").color(colorSelected));
            else current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 1")));

            if (mode == 0) current_page.append(Component.text("  [1-2]").color(colorSelected));
            else current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0")));

            if (mode == 2) current_page.append(Component.text("  [★★★]").color(colorSelected));
            else current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));
        }

        pages.add(current_page);

        player.openBook(Book.book(Component.empty(),Component.empty(), pages));
    }

}
