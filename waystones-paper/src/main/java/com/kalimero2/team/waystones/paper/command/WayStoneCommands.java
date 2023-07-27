package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public class WayStoneCommands extends CommandHandler {

    private WaystonesScreen screen;
    private DisplayManager display;
    private Storage storage;


    public WayStoneCommands(PaperWayStones plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        screen = new WaystonesScreen(plugin);
        display = new DisplayManager(plugin);
        storage = plugin.getStorage();
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("menu")
                .senderType(Player.class)
                .handler(this::menu)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("force")
                .senderType(Player.class)
                .permission("waystones.admin")
                .handler(this::forceMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("give")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("give")
                .literal("static")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("give")
                .literal("portable")
                .permission("waystones.give")
                .handler(this::givePortableWaystone)
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
                .argument(StringArgument.of("term"))
                .handler(this::searchWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("edit")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::editWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("rename")
                .argument(WaystoneArgument.of("waystone"))
                .argument(StringArgument.of("newname"))
                .handler(this::renameWayStone)
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
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("remove")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::removeWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("public")
                .handler(this::changeVisibilityToPublic)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("unlisted")
                .handler(this::changeVisibilityToUnlisted)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("private")
                .handler(this::changeVisibilityToPrivate)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("add")
                .argument(PlayerArgument.of("player"))
                .handler(this::addPlayerToWhitelist)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("remove")
                .argument(PlayerArgument.of("player"))
                .handler(this::removePlayerFromWhitelist)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("whitelist")
                .argument(WaystoneArgument.of("waystone"))
                .literal("list")
                .handler(this::listWhitelist)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("display")
                .literal("update")
                .literal("all")
                .permission("waystones.display")
                .handler(this::reloadAllDisplays)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("display")
                .literal("update")
                .literal("waystone")
                .argument(WaystoneArgument.of("waystone"))
                .permission("waystones.display")
                .handler(this::reloadDisplay)
        );
        commandManager.command(commandManager.commandBuilder("waystone")
                .literal("display")
                .literal("clear")
                .permission("waystones.display")
                .handler(this::clearDisplays)
        );
    }

    private void forceMode(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        if (storage.forceMode(player, !storage.forceMode(player))) {
            player.sendMessage(Component.text("Force Modus aktiviert").color(TextColor.color(0, 255, 50)));
        }
        else player.sendMessage(Component.text("Force Modus deaktiviert").color(TextColor.color(0, 255, 50)));

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

    private void menu(CommandContext<CommandSender> context) {
        screen.menu((Player) context.getSender(), null);
    }


    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");

            boolean teleportAllowed = storage.forceMode(player);

            for (StoredWaystone w : storage.getWaystones(player.getWorld().getUID())) {
                if (w.location().distance(player.getLocation()) <= 5) teleportAllowed = true;
            }

            if (!teleportAllowed) {
                for (ItemStack stack : player.getInventory()) {
                    if (stack != null) {
                        if (stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "portable"))) {
                            if (player.getLevel() >= 1) {
                                player.setLevel(player.getLevel() - 1);
                                teleportAllowed = true;
                            }
                            else {
                                player.sendMessage(Component.text("Du benötigst mindestens ein Level, um dich zu teleportieren!").color(TextColor.color(255, 73, 0)));
                            }
                        }
                    }
                }
            }

            if (!teleportAllowed) {
                player.sendMessage(Component.text("Du musst dich zum teleportieren in der Nähe eines Waystones befinden!").color(TextColor.color(255, 73, 0)));
                return;
            }

            if (waystone.checkPlayer(player) || storage.forceMode(player)) {
                player.teleport(waystone.location());
            }
        }
    }

    private void editWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");
            if (waystone.owner().equals(player.getUniqueId()) || storage.forceMode(player)) {
                screen.settings(player, waystone);
            }
        }
    }

    private void renameWayStone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        String newName = context.get("newname");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !storage.forceMode(player)) return;
        }

        if (storage.nameFree(newName)) {
            storage.renameWaystone(waystone.id(), newName);
            display.updateDisplay(waystone);
            sender.sendMessage(Component.text("Waystone " + waystone.name() + " mit der ID " + waystone.id() + " wurde in " + newName + " umbenannt").color(TextColor.color(18, 255, 36)));
        }

        else {
            context.getSender().sendMessage(Component.translatable("waystones.create.name.taken").fallback("Dieser Name ist bereits vergeben!").asComponent().color(TextColor.color(255, 73, 0)));
        }
    }


    private void searchWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            screen.list(player, context.get("term"));
        }
    }

    private void addFavorite(CommandContext<CommandSender> context) {
        plugin.getStorage().addFavorite((Player) context.getSender(), context.get("id"));
        screen.menu((Player) context.getSender(), null);
    }

    private void removeFavorite(CommandContext<CommandSender> context) {
        plugin.getStorage().removeFavorite((Player) context.getSender(), context.get("id"));
        screen.menu((Player) context.getSender(), null);
    }

    private void sortingMode(CommandContext<CommandSender> context) {
        plugin.getStorage().setSortMode((Player) context.getSender(), SortMode.valueByNumber(context.get("mode")));
        screen.menu((Player) context.getSender(), null);
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

    private void givePortableWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if (sender instanceof Player player) {
            player.getInventory().addItem(plugin.getPortable());
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

        Player player = (Player) context.getSender();

        storage.addWaystone(name, player.getUniqueId(), 0, location.getChunk().getX(), location.getChunk().getZ(), location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
        StoredWaystone waystone = storage.getWaystone(location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());

        display.updateDisplay(waystone);

        context.getSender().sendMessage(Component.text("Waystone created at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " named " + name));
    }


    private void removeWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.remove")) return;
            player.getInventory().addItem(plugin.getItem());
        }

        display.clearDisplay(waystone);
        storage.removeWaystone(waystone.id());

        if (context.getSender() instanceof Player player) {
            if (!(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))) {
                player.getInventory().addItem(plugin.getItem());
            }
        }

        sender.sendMessage(Component.text("Waystone mit ID " + waystone.id() + " wurde entfernt.").color(TextColor.color(255, 73, 0)));
    }

    private void changeVisibility(CommandContext<CommandSender> context, Visibility visibility) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.remove")) return;
        }

        storage.setVisibility(waystone.id(), visibility);

        sender.sendMessage(Component.text("Waystone mit ID " + waystone.id() + " ist jetzt " + visibility.text()).color(TextColor.color(255, 73, 0)));
    }

    private void changeVisibilityToPublic(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.PUBLIC);}
    private void changeVisibilityToUnlisted(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.UNLISTED);}
    private void changeVisibilityToPrivate(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.PRIVATE);}

    private void addPlayerToWhitelist(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        Player player = context.get("player");

        if (sender instanceof Player player2) {
            if (!waystone.owner().equals(player2.getUniqueId()) && !player2.hasPermission("waystones.admin")) return;
        }

        storage.addWhitelist(player, waystone.id());
        sender.sendMessage(Component.text("Spieler " + player.getName() + " wurde auf die Whitelist vom Waystone " + waystone.id() + " gesetzt.").color(TextColor.color(18, 255, 36)));
    }

    private void removePlayerFromWhitelist(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        Player player = context.get("player");

        if (sender instanceof Player player2) {
            if (!waystone.owner().equals(player2.getUniqueId()) && !player2.hasPermission("waystones.admin")) return;
        }

        storage.removeWhitelist(player, waystone.id());
        sender.sendMessage(Component.text("Spieler " + player.getName() + " wurde von der Whitelist vom Waystone " + waystone.id() + " entfernt.").color(TextColor.color(18, 255, 36)));
    }

    private void listWhitelist(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !storage.forceMode(player)) return;
        }

        sender.sendMessage(Component.text("Folgende Spieler sind auf der Whitelist von Waystone " + waystone.id() + ":").color(TextColor.color(18, 255, 36)));

        for (Player p : storage.whitelist(waystone.id())) {
            sender.sendMessage(Component.text(p.getName()).hoverEvent(HoverEvent.showText(Component.text(p.getUniqueId().toString()))));
        }

    }

    private void reloadDisplay(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        display.updateDisplay(waystone);
        context.getSender().sendMessage(Component.text("Updating display of waystone " + waystone.id()).color(TextColor.color(18, 255, 36)));
    }

    private void reloadAllDisplays(CommandContext<CommandSender> context) {
        display.updateAll();
        context.getSender().sendMessage(Component.text("Updating display of all waystones...").color(TextColor.color(18, 255, 36)));
    }

    private void clearDisplays(CommandContext<CommandSender> context) {
        display.removeAll();
        context.getSender().sendMessage(Component.text("Removing displays in all loaded chunks...").color(TextColor.color(18, 255, 36)));
    }
}
