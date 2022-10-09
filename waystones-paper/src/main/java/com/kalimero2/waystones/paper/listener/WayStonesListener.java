package com.kalimero2.waystones.paper.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent;
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent;
import com.kalimero2.waystones.paper.BedrockCompat;
import com.kalimero2.waystones.paper.PaperWayStones;
import com.kalimero2.waystones.paper.SerializableWayStone;
import com.kalimero2.waystones.paper.SerializableWayStones;
import com.kalimero2.waystones.paper.WayStoneDataTypes;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
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
        if(event.getBlock().getType() == Material.STONE_BRICK_WALL && event.getItemInHand().isSimilar(PaperWayStones.plugin.getItem())){
            Player player = event.getPlayer();


            Location location = event.getBlock().getLocation();

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
    public void onInteract(PlayerInteractAtEntityEvent event){
        if(event.getRightClicked().getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY)){
            event.setCancelled(true);

            if(PaperWayStones.plugin.bedrockSupport){
                boolean bedrock =  org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
                if(bedrock){
                    BedrockCompat.showBedrockForm(event.getPlayer());
                }
            }
            showJavaBook(event.getPlayer());

        }
    }

    private static void showJavaBook(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
        for (Map.Entry<Integer, Location> entry : wayStones.getWayStones().entrySet()) {
            Integer integer = entry.getKey();
            Location location = entry.getValue();
            CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), PaperWayStones.plugin);
            SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
            counter++;
            if(counter == 14){
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            current_page = current_page.append(Component.text(wayStone.getName()).clickEvent(ClickEvent.runCommand("/waystone tp " + integer)).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren"))));
            current_page = current_page.append(Component.newline());
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(),Component.empty(), pages));
    }

}
