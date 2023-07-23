package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class WayStoneCommands extends CommandHandler {

    private WaystonesScreen screen;
    private Storage storage;


    public WayStoneCommands(PaperWayStones plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        screen = new WaystonesScreen(plugin);
        storage = plugin.getStorage();
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
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::teleportToWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("favorite")
                .literal("add")
                .senderType(Player.class)
                .argument(IntegerArgument.of("id"))
                .handler(this::addFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("favorite")
                .literal("remove")
                .senderType(Player.class)
                .argument(IntegerArgument.of("id"))
                .handler(this::removeFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("sortingmode")
                .senderType(Player.class)
                .argument(IntegerArgument.of("mode"))
                .handler(this::sortingMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("search")
                .senderType(Player.class)
                .handler(this::searchMenu)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("search")
                .senderType(Player.class)
                .argument(StringArgument.of("term"))
                .handler(this::searchWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("edit")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::editWayStone)
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
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("create")
                .argument(LocationArgument.of("location"))
                .argument(StringArgument.of("name"))
                .handler(this::createWaystone)
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
            storage.removeWaystone(context.get("id"));
        }
    }

    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");
            if (waystone.checkPlayer(player)) {
                player.teleport(waystone.location());
            }
        }
    }

    private void editWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");
            if (waystone.owner().equals(player.getUniqueId())) {
                screen.settings(player, waystone);
            }
        }
    }

    private void searchMenu(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            screen.search(player);
        }
    }

    private void searchWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            screen.list(player, context.get("term"));
        }
    }

    private void addFavorite(CommandContext<CommandSender> context) {
        plugin.getStorage().addFavorite((Player) context.getSender(), context.get("id"));
    }

    private void removeFavorite(CommandContext<CommandSender> context) {
        plugin.getStorage().removeFavorite((Player) context.getSender(), context.get("id"));

    }

    private void sortingMode(CommandContext<CommandSender> context) {
        plugin.getStorage().setSortMode((Player) context.getSender(), SortMode.valueByNumber(context.get("mode")));
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

    private void createWaystone(CommandContext<CommandSender> context) {
        Location location = context.get("location");
        String name = context.get("name");
        World world = location.getWorld();

        if (!storage.nameFree(name)) {
            context.getSender().sendMessage(Component.translatable("waystones.create.name.taken").fallback("Dieser Name ist bereits vergeben!").asComponent().color(TextColor.color(255, 73, 0)));
            return;
        }

        Location centerLocation = location.toCenterLocation();
        Location topLocation = centerLocation.clone().add(0, 1, 0);
        Block centerLocationBlock = centerLocation.getBlock();
        centerLocationBlock.setType(Material.BARRIER);
        Block topLocationBlock = topLocation.getBlock();
        topLocationBlock.setType(Material.BARRIER);

        // IMPORTANT: METADATA IS NOT PERSISTENT
        centerLocationBlock.setMetadata("waystoneID", new FixedMetadataValue(plugin, 0));
        topLocationBlock.setMetadata("waystoneID", new FixedMetadataValue(plugin, 0));

        ItemDisplay itemDisplay = world.spawn(centerLocation, ItemDisplay.class);
        itemDisplay.setItemStack(plugin.getItem());

        UUID itemDisplayUniqueId = itemDisplay.getUniqueId();

        Location textDisplayLocation = topLocation.clone().add(0, 0.75, 0);
        TextDisplay textDisplay = world.spawn(textDisplayLocation, TextDisplay.class);
        textDisplay.text(Component.text(name));
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        UUID textDisplayUniqueId = textDisplay.getUniqueId();

        context.getSender().sendMessage("Waystone created at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " named " + name);
    }
}
