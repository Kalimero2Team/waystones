package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WayStoneCommands extends CommandHandler {
    public WayStoneCommands(PaperWayStones plugin, CommandManager commandManager) {
        super(plugin, commandManager);
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
                .argument(WorldArgument.optional("world"))
                .handler(this::listWaystones)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("tp")
                .argument(IntegerArgument.of("id"))
                .handler(this::teleportToWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("remove")
                .permission("waystones.remove")
                .argument(IntegerArgument.of("id"))
                .handler(this::removeWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("openTestInv")
                .handler(this::openTestInv)
        );
    }

    private void openTestInv(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player) {
            Component title = MiniMessage.miniMessage().deserialize("<white><font:klm2:waystones>b</font>");
            player.openInventory(plugin.getServer().createInventory(null, 9*2, Component.translatable("space.-8").append(title)));
            /* Now with a book
            Book book = Book.builder()
                    .title(Component.text("Test Book"))
                    .author(Component.text("Test Author"))
                    .addPage(Component.translatable("offset.-20").append(title))
                    .build();
            player.openBook(book);

             */
        }
    }

    private void removeWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            Integer id = context.get("id");
            // TODO: Remove Waystone
        }
    }

    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = plugin.getStorage().getWaystone(context.get("id"));
            if (waystone != null) {
                player.teleport(waystone.location());
            }
        }
    }

    private void listWaystones(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            World world = context.getOrDefault("world", player.getWorld());

            player.sendMessage("Waystones in " + world.getName() + ":");

            StoredWaystone[] waystones = plugin.getStorage().getWaystones(world.getUID());
            for (StoredWaystone waystone : waystones) {
                player.sendMessage(Component.text("Waystone " + waystone.id() + ": " + waystone.name() + " (" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + ")" + " Owner: " + waystone.owner()).clickEvent(ClickEvent.runCommand("/tp " + waystone.block_x() + " " + waystone.block_y() + " " + waystone.block_z())));
            }
        }
    }

    private void giveWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if (sender instanceof Player player) {
            player.getInventory().addItem(plugin.getItem());
        }
    }
}
