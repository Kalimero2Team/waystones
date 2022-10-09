package com.kalimero2.waystones.paper.command;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.jeff_media.customblockdata.CustomBlockData;
import com.kalimero2.waystones.paper.PaperWayStones;
import com.kalimero2.waystones.paper.SerializableWayStone;
import com.kalimero2.waystones.paper.SerializableWayStones;
import com.kalimero2.waystones.paper.WayStoneDataTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

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
                .literal("tp")
                .argument(StringArgument.of("id"))
                .handler(this::teleportToWayStone)
        );
    }

    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player) {
            SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
            Location location = wayStones.getWayStone(Integer.parseInt(context.get("id")));
            if (location != null) {
                if(player.getLocation().getNearbyEntitiesByType(FallingBlock.class,5).stream().anyMatch(entity -> entity.getPersistentDataContainer().has(PaperWayStones.WAYSTONE_KEY))){
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
}
