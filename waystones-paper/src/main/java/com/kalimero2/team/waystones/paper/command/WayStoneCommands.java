package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.jeff_media.customblockdata.CustomBlockData;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.SerializableWayStone;
import com.kalimero2.team.waystones.paper.SerializableWayStones;
import com.kalimero2.team.waystones.paper.WayStoneDataTypes;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import com.kalimero2.team.waystones.paper.listener.WayStonesListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WayStoneCommands extends CommandHandler{
    public WayStoneCommands(PaperWayStones wayStones, CommandManager commandManager) {
        super(wayStones, commandManager);
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("give")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("list")
                .permission("waystones.list")
                .handler(this::listWaystones)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("setVillager")
                .permission("waystones.setVillager")
                .handler(this::setVillager)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("tp")
                .argument(StringArgument.of("id"))
                .handler(this::teleportToWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("remove")
                .permission("waystones.remove")
                .argument(IntegerArgument.of("id"))
                .handler(this::removeWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("favorite")
                .argument(IntegerArgument.of("id"))
                .handler(this::switchFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("sortingmode")
                .argument(IntegerArgument.of("id"))
                .handler(this::setSortMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("favoriteselectionmenu")
                .handler(this::switchFavoriteMenu)
        );
    }

    private void removeWayStone(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            Integer id = context.get("id");
            SerializableWayStones serializableWayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
            serializableWayStones.removeWayStone(id);
            player.sendMessage(Component.text("Removed waystone " + id));
            PaperWayStones.plugin.setSerializableWayStones(player.getWorld(), serializableWayStones);
        }
    }

    private void setVillager(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            Entity targetEntity = player.getTargetEntity(4);
            if(targetEntity instanceof Villager villager){
                villager.getPersistentDataContainer().set(PaperWayStones.WAYSTONE_VILLAGER, PersistentDataType.BYTE, (byte) 1);
                player.sendMessage("Villager is now a Waystone Villager");
            }
        }
    }

    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player) {
            SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
            Location location = wayStones.getWayStone(Integer.parseInt(context.get("id")));
            if (location != null) {
                if(player.getLocation().getNearbyEntitiesByType(ArmorStand.class,5).stream().anyMatch(entity -> entity.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY))){
                    player.teleportAsync(location.clone().add(0, 1, 0));
                }
            } else {
                player.sendMessage("Waystone not found");
            }
        }
    }

    private void listWaystones(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player){
            player.sendMessage("Waystones:");
            SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
            wayStones.getWayStones().forEach((integer, location) -> {
                SerializableWayStone wayStone = new CustomBlockData(location.getBlock(), PaperWayStones.plugin).get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
                player.sendMessage(Component.text("Waystone " + integer + ": " + wayStone.getName()+ " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")"+" Owner: "+wayStone.getOwnerUUID()).clickEvent(ClickEvent.runCommand("/tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ())));
            });
        }
    }

    private void giveWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if(sender instanceof Player player){
            player.getInventory().addItem(PaperWayStones.plugin.getItem());
        }
    }
    
    
    private void switchFavorite(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            
            int[] oldList = dataContainer.get(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY);
            if (oldList == null) oldList = new int[0];
            
            int waystone_id = context.get("id");
            boolean added = false;
            
            for (int id : oldList) {
                if (id == waystone_id) {
                    added = true;
                }
            }
            
            if (added) {
                for (int id : oldList) {
                    if (id == waystone_id) {
    
                        added = true;
                        int[] newList = new int[oldList.length - 1];
    
                        int shift = 0;
    
                        for (int i = 0; i < oldList.length-1; i++) {
                            if (oldList[i] == waystone_id) shift = 1;
                            newList[i] = oldList[i+shift];
                        }
    
                        dataContainer.set(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY, newList);
                    }
                }
            }
            
            else {
                if (oldList.length < 12) {
                    int[] newList = new int[oldList.length + 1];
                    for (int i = 0; i < oldList.length; i++) {
                        newList[i] = oldList[i];
                    }
                    newList[oldList.length] = waystone_id;
                    dataContainer.set(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY, newList);
                }
                else {
                    player.sendMessage("Maximum number of favorite waystones is 12");
                }
                
            }
            openMenu(player);
        }
    }

    
    // No longer needed because switchFavorite() is used:
    /*
    private void addFavorite(Player player, int waystone) {
        if (context.getSender() instanceof Player player) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            int[] oldList = dataContainer.get(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY);
            if (oldList == null) oldList = new int[0];
            int waystone_id = context.get("id");
            boolean added = false;
            for (int id : oldList) {
                if (id == waystone_id) {
                    added = true;
                }
            }
            if (!added) {
                if (oldList.length < 12) {
                    int[] newList = new int[oldList.length + 1];
                    for (int i = 0; i < oldList.length; i++) {
                        newList[i] = oldList[i];
                    }
                    newList[oldList.length] = waystone_id;
                    dataContainer.set(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY, newList);
                }
                else {
                    player.sendMessage("Maximum number of favorite waystones is 12");
                }
            }
            openMenu(player);
        }
    }


    private void removeFavorite(Player player, int waystone) {
        if (context.getSender() instanceof Player player) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            int[] oldList = dataContainer.get(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY);
            if (oldList == null) oldList = new int[0];
            int waystone_id = context.get("id");
            boolean added = false;

            for (int id : oldList) {

                if (id == waystone_id) {

                    added = true;
                    int[] newList = new int[oldList.length - 1];

                    int shift = 0;

                    for (int i = 0; i < oldList.length-1; i++) {
                        if (oldList[i] == waystone_id) shift = 1;
                        newList[i] = oldList[i+shift];
                    }

                    dataContainer.set(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY, newList);
                }
            }

            if (!added) {
            }
            openMenu(player);
        }
    }
    */

    private void setSortMode(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            dataContainer.set(new NamespacedKey("waystones", "sorting_mode"), PersistentDataType.INTEGER, context.get("id"));
            openMenu(player);
        }
    }


    private void switchFavoriteMenu(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            NamespacedKey key = new NamespacedKey("waystones", "favorite_selection_mode");
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();
            if (dataContainer.has(key)) {
                if (dataContainer.get(key, PersistentDataType.INTEGER) == 0) dataContainer.set(key, PersistentDataType.INTEGER, 1);
                else dataContainer.set(key, PersistentDataType.INTEGER, 0);
            }
            else dataContainer.set(key, PersistentDataType.INTEGER, 1);
            
            openMenu(player);
        }
    }


    private void openMenu(Player player) {
        if (PaperWayStones.plugin.bedrockIntegration) {
            boolean bedrock = org.geysermc.floodgate.api.FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
            if (bedrock) {
                FloodgateIntegration.showBedrockForm(player);
            }
            else {
                WayStonesListener.showJavaBook(player);
            }
        }
    }
}
