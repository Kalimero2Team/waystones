package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.bukkit.parsers.location.LocationArgument;
import cloud.commandframework.context.CommandContext;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.command.arguments.WaystoneArgument;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import com.kalimero2.team.waystones.paper.ui.java.TestScreen;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.ColorUtil;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WayStoneCommands extends CommandHandler {

    private final WaystonesScreen screen;
    private final DisplayManager display;
    private final WaystoneManager manager;


    public WayStoneCommands(PaperWayStones plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        screen = new WaystonesScreen(plugin);
        display = new DisplayManager(plugin);
        manager = plugin.getManager();
    }

    @Override
    public void register() {
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("menu")
                .senderType(Player.class)
                .handler(this::menu)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("force")
                .senderType(Player.class)
                .permission("waystones.admin")
                .handler(this::forceMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .literal("static")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .literal("portable")
                .permission("waystones.give")
                .handler(this::givePortableWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("list")
                .permission("waystones.list")
                .argument(WorldArgument.optional("world"))
                .handler(this::listWaystones)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("tp")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::teleportToWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("favorite")
                .literal("add")
                .senderType(Player.class)
                .argument(IntegerArgument.of("id"))
                .handler(this::addFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("favorite")
                .literal("remove")
                .senderType(Player.class)
                .argument(IntegerArgument.of("id"))
                .handler(this::removeFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("sortingmode")
                .senderType(Player.class)
                .argument(IntegerArgument.of("mode"))
                .handler(this::sortingMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("search")
                .senderType(Player.class)
                .handler(this::searchWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("search")
                .senderType(Player.class)
                .argument(StringArgument.of("term"))
                .handler(this::searchWayStoneTerm)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("edit")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::editWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("rename")
                .argument(WaystoneArgument.of("waystone"))
                .argument(StringArgument.of("newname"))
                .handler(this::renameWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("openTestInv")
                .handler(this::openTestInv)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("create")
                .argument(LocationArgument.of("location"))
                .argument(StringArgument.of("name"))
                .handler(this::createWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("remove")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::removeWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("public")
                .handler(this::changeVisibilityToPublic)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("unlisted")
                .handler(this::changeVisibilityToUnlisted)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("private")
                .handler(this::changeVisibilityToPrivate)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .argument(WaystoneArgument.of("waystone"))
                .literal("add")
                .argument(PlayerArgument.of("player"))
                .handler(this::addPlayerToAccessList)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .argument(WaystoneArgument.of("waystone"))
                .literal("remove")
                .argument(PlayerArgument.of("player"))
                .handler(this::removePlayerFromAccessList)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .argument(WaystoneArgument.of("waystone"))
                .literal("list")
                .handler(this::showAccessList)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("display")
                .literal("update")
                .literal("all")
                .permission("waystones.display")
                .handler(this::reloadAllDisplays)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("display")
                .literal("update")
                .literal("waystone")
                .argument(WaystoneArgument.of("waystone"))
                .permission("waystones.display")
                .handler(this::reloadDisplay)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("display")
                .literal("clear")
                .permission("waystones.display")
                .handler(this::clearDisplays)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("add")
                .argument(StringArgument.of("name"))
                .argument(BooleanArgument.of("public"))
                .permission("waystones.category")
                .handler(this::addCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("remove")
                .argument(StringArgument.of("name"))
                .argument(BooleanArgument.of("public"))
                .permission("waystones.category")
                .handler(this::removeCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("set")
                .argument(WaystoneArgument.of("waystone"))
                .argument(IntegerArgument.of("category"))
                .handler(this::setCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("remove")
                .argument(WaystoneArgument.of("waystone"))
                .senderType(Player.class)
                .handler(this::buttonRemoveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("rename")
                .argument(WaystoneArgument.of("waystone"))
                .senderType(Player.class)
                .handler(this::buttonRenameWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("access")
                .literal("add")
                .argument(WaystoneArgument.of("waystone"))
                .senderType(Player.class)
                .handler(this::buttonAddAccess)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("transferownership")
                .argument(WaystoneArgument.of("waystone"))
                .senderType(Player.class)
                .handler(this::buttonTransferOwnership)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("category")
                .argument(WaystoneArgument.of("waystone"))
                .senderType(Player.class)
                .handler(this::categorySelection)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("creation")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("public")
                .senderType(Player.class)
                .handler(this::changeVisibilityToPublicRedirectToCategorySelection)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("creation")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("unlisted")
                .senderType(Player.class)
                .handler(this::changeVisibilityToUnlistedRedirectToCategorySelection)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("creation")
                .literal("visibility")
                .argument(WaystoneArgument.of("waystone"))
                .literal("private")
                .senderType(Player.class)
                .handler(this::changeVisibilityToPrivateRedirectToCategorySelection)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .literal("edit")
                .argument(WaystoneArgument.of("waystone"))
                .handler(this::buttonEditAccesslist)
        );
    }

    private void forceMode(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        if (manager.toggleForceMode(player)) {
            player.sendMessage(Component.translatable("waystones.force.on", ColorUtil.GREEN));
        }
        else player.sendMessage(Component.translatable("waystones.force.off", ColorUtil.GREEN));

    }

    private void openTestInv(CommandContext<CommandSender> context) {
        if(context.getSender() instanceof Player player) {
            /*Component title = MiniMessage.miniMessage().deserialize("<white><font:klm2:waystones>b</font><reset><lang:space.-170>Edit Waystone");
            player.openInventory(plugin.getServer().createInventory(null, 9*2, Component.translatable("space.-8").append(title)));*/
            new TestScreen(plugin).show(player);
        }
    }

    private void menu(CommandContext<CommandSender> context) {
        screen.menu((Player) context.getSender(), null);
    }


    private void teleportToWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");

            boolean teleportAllowed = manager.forceMode(player);

            for (StoredWaystone w : manager.getWaystones(player.getWorld().getUID())) {
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
                                player.sendMessage(Component.translatable("waystones.teleport.noxp", ColorUtil.ORANGE));
                                return;
                            }
                        }
                    }
                }
            }

            if (!teleportAllowed) {
                player.sendMessage(Component.translatable("waystones.teleport.nowaystone", ColorUtil.ORANGE));
                return;
            }

            if (waystone.checkTeleport(player) || manager.forceMode(player)) {
                player.teleport(waystone.location());
                manager.addTeleport(player, waystone.id());
            }
        }
    }

    private void editWayStone(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            StoredWaystone waystone = context.get("waystone");
            if (waystone.owner().equals(player.getUniqueId()) || manager.forceMode(player)) {
                screen.settings(player, waystone);
            }
        }
    }

    private void renameWayStone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        String newName = context.get("newname");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !manager.forceMode(player)) return;
        }

        if (manager.renameWaystone(waystone.id(), newName)) {
            display.updateDisplay(waystone);
            sender.sendMessage(Component.translatable("waystones.ui.name.rename", ColorUtil.GREEN, Component.text(waystone.id()), Component.text(waystone.name()), Component.text(newName)));
        }

        else {
            context.getSender().sendMessage(Component.translatable("waystones.create.name.taken", ColorUtil.ORANGE, Component.text(newName)));
        }
    }


    private void searchWayStone(CommandContext<CommandSender> context) {
        screen.search((Player) context.getSender());
    }

    private void searchWayStoneTerm(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            screen.list(player, context.get("term"));
        }
    }

    private void addFavorite(CommandContext<CommandSender> context) {
        manager.addFavorite((Player) context.getSender(), context.get("id"));
        screen.menu((Player) context.getSender(), null);
    }

    private void removeFavorite(CommandContext<CommandSender> context) {
        manager.removeFavorite((Player) context.getSender(), context.get("id"));
        screen.menu((Player) context.getSender(), null);
    }

    private void sortingMode(CommandContext<CommandSender> context) {
        manager.setSortMode((Player) context.getSender(), SortMode.valueByNumber(context.get("mode")));
        screen.menu((Player) context.getSender(), null);
    }

    private void listWaystones(CommandContext<CommandSender> context) {
        if (context.getSender() instanceof Player player) {
            World world = context.getOrDefault("world", player.getWorld());

            player.sendMessage("Waystones in " + world.getName() + ":");

            List<StoredWaystone> waystones = manager.getWaystones(world.getUID());
            for (StoredWaystone waystone : waystones) {
                player.sendMessage(Component.text("Waystone " + waystone.id() + ": " + waystone.name() + " (" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + ")" + " Owner: " + waystone.owner()).clickEvent(ClickEvent.runCommand("/tp " + waystone.block_x() + " " + waystone.block_y() + " " + waystone.block_z())));
            }
        }
    }

    private void giveWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        if (sender instanceof Player player) {
            player.getInventory().addItem(plugin.getStatic());
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

        if (manager.isNameUsed(name)) {
            context.getSender().sendMessage(Component.translatable("waystones.ui.name.taken", TextColor.color(255, 73, 0), Component.text(name)));
            return;
        }

        Player player = (Player) context.getSender();

        manager.createWaystone(name, player.getUniqueId(), 1, -1, location);
        StoredWaystone waystone = manager.getWaystone(location);

        display.updateDisplay(waystone);

        context.getSender().sendMessage(Component.translatable("waystones.ui.create", ColorUtil.GREEN, Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()), Component.text(name), Component.text(waystone.id())));
    }


    private void removeWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.remove")) return;
            player.getInventory().addItem(plugin.getStatic());
        }

        display.clearDisplay(waystone);
        manager.removeWaystone(waystone.id());

        if (context.getSender() instanceof Player player) {
            if (!(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))) {
                player.getInventory().addItem(plugin.getStatic());
            }
        }

        sender.sendMessage(Component.translatable("waystones.remove", TextColor.color(255, 73, 0), Component.text(waystone.id())));
    }

    private void changeVisibility(CommandContext<CommandSender> context, Visibility visibility) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.remove")) return;
        }

        manager.setVisibility(waystone.id(), visibility);

        sender.sendMessage(Component.translatable("waystones.visibility.set", TextColor.color(255, 73, 0), Component.text(waystone.id()), visibility.text()));
    }

    private void changeVisibilityToPublic(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.PUBLIC);}
    private void changeVisibilityToUnlisted(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.UNLISTED);}
    private void changeVisibilityToPrivate(CommandContext<CommandSender> context) {changeVisibility(context, Visibility.PRIVATE);}

    private void changeVisibilityToPublicRedirectToCategorySelection(CommandContext<CommandSender> context) {
        changeVisibility(context, Visibility.PUBLIC);
        categorySelection(context);
    }
    private void changeVisibilityToUnlistedRedirectToCategorySelection(CommandContext<CommandSender> context) {
        changeVisibility(context, Visibility.UNLISTED);
        categorySelection(context);
    }
    private void changeVisibilityToPrivateRedirectToCategorySelection(CommandContext<CommandSender> context) {
        changeVisibility(context, Visibility.PRIVATE);
        categorySelection(context);
    }


    private void categorySelection(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.category((Player) context.getSender(), waystone);
    }


    private void addPlayerToAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        Player player = context.get("player");

        if (sender instanceof Player player2) {
            if (!waystone.owner().equals(player2.getUniqueId()) && !player2.hasPermission("waystones.admin")) return;
        }

        manager.addAccess(player, waystone.id());
        sender.sendMessage(Component.translatable("waystones.access.add", ColorUtil.GREEN, player.displayName(), Component.text(waystone.id())));
    }

    private void removePlayerFromAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");
        Player player = context.get("player");

        if (sender instanceof Player player2) {
            if (!waystone.owner().equals(player2.getUniqueId()) && !player2.hasPermission("waystones.admin")) return;
        }

        manager.removeAccess(player, waystone.id());
        sender.sendMessage(Component.translatable("waystones.access.remove", ColorUtil.GREEN, player.displayName(), Component.text(waystone.id())));
    }

    private void showAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !manager.forceMode(player)) return;
        }

        List<OfflinePlayer> list = manager.getAccess(waystone.id());

        if (!list.isEmpty()) sender.sendMessage(Component.translatable("waystones.access.list", ColorUtil.GREEN));
        else sender.sendMessage(Component.translatable("waystones.access.list.empty", ColorUtil.GREEN));

        for (OfflinePlayer p : list) {
            sender.sendMessage(Component.text(p.getName()).hoverEvent(HoverEvent.showText(Component.text(p.getUniqueId().toString()))));
        }

    }

    private void reloadDisplay(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        display.updateDisplay(waystone);
        context.getSender().sendMessage(Component.translatable("waystones.display.reload.waystone", ColorUtil.GREEN, Component.text(waystone.id())));
    }

    private void reloadAllDisplays(CommandContext<CommandSender> context) {
        display.updateAll();
        context.getSender().sendMessage(Component.translatable("waystones.display.reload.all", ColorUtil.GREEN));
    }

    private void clearDisplays(CommandContext<CommandSender> context) {
        display.removeAll();
        context.getSender().sendMessage(Component.translatable("waystones.display.clear", ColorUtil.GREEN));
    }


    private void addCategory(CommandContext<CommandSender> context) {
        if (manager.addCategory(context.get("name"), context.get("public")) != null) {
            String type = "restricted";
            if (context.get("public")) type = "public";
            context.getSender().sendMessage(Component.translatable("waystones.category.add." + type, ColorUtil.GREEN, Component.text(context.get("name").toString()).color(TextColor.color(255, 255, 255))));
        }
        else {
            context.getSender().sendMessage(Component.translatable("waystones.category.nametaken", Component.text(context.get("name").toString())));
        }
    }

    private void removeCategory(CommandContext<CommandSender> context) {
        manager.addCategory(context.get("name"), context.get("public"));
        context.getSender().sendMessage(Component.translatable("waystones.category.remove", ColorUtil.GREEN,  Component.text(context.get("name").toString()).color(TextColor.color(255, 255, 255))));
    }

    private void setCategory(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        if (waystone.checkPermission(context.getSender())) {
            Category category = manager.getCategory((int) context.get("category"));
            if (category == null) {
                context.getSender().sendMessage(Component.translatable("waystones.category.invalid"));
                return;
            }
            manager.updateWaystone(new StoredWaystone(waystone.id(), waystone.name(), waystone.owner(), waystone.visibility(), category, waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
            context.getSender().sendMessage(Component.translatable("waystones.category.set", ColorUtil.GREEN, Component.text(waystone.name()), Component.text(category.name())));
            return;
        }
        context.getSender().sendMessage(Component.translatable("waystones.permission.edit", ColorUtil.RED, Component.text(context.get("name").toString()).color(ColorUtil.WHITE)));
    }

    private void buttonRemoveWaystone(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        context.getSender().sendMessage(Component.translatable("waystones.remove.ask", ColorUtil.RED, Component.text(waystone.name())));
        context.getSender().sendMessage(Component.translatable("waystones.remove.confirm").clickEvent(ClickEvent.runCommand("/waystone remove " + waystone.id())));
    }

    private void buttonRenameWaystone(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.rename((Player) context.getSender(), waystone);
    }

    private void buttonAddAccess(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.addAccess((Player) context.getSender(), waystone);
    }


    private void buttonEditAccesslist(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.accessSettings((Player) context.getSender(), waystone);
    }

    private void buttonTransferOwnership(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.transferOwnership((Player) context.getSender(), waystone);
    }
}
