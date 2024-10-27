package com.kalimero2.team.waystones.paper.command;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.command.argument.WaystoneComponent;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.bukkit.data.SingleEntitySelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.bukkit.parser.selector.SingleEntitySelectorParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.List;
import java.util.Objects;

public class WayStoneCommands extends CommandHandler {

    private final WaystonesScreen screen;
    private final DisplayManager display;
    private final WaystoneManager manager;


    public WayStoneCommands(PaperWayStones plugin, LegacyPaperCommandManager<CommandSender> commandManager) {
        super(plugin, commandManager);
        screen = plugin.getScreen();
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
                .literal("menu")
                .literal("category")
                .senderType(Player.class)
                .handler(this::browseCategorySelect)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("menu")
                .literal("category")
                .required("category", IntegerParser.integerParser())
                .senderType(Player.class)
                .handler(this::browseCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("force")
                .senderType(Player.class)
                .permission("waystones.admin")
                .handler(this::forceMode)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .senderType(Player.class)
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .senderType(Player.class)
                .literal("static")
                .permission("waystones.give")
                .handler(this::giveWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("give")
                .senderType(Player.class)
                .literal("portable")
                .permission("waystones.give")
                .handler(this::givePortableWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("list")
                .senderType(Player.class)
                .permission("waystones.list")
                .optional("world", WorldParser.worldParser())
                .handler(this::listWaystones)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("tp")
                .senderType(Player.class)
                .required(WaystoneComponent.of("waystone"))
                .handler(this::teleportToWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("favorite")
                .literal("add")
                .senderType(Player.class)
                .required(WaystoneComponent.of("waystone"))
                .handler(this::addFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("favorite")
                .literal("remove")
                .senderType(Player.class)
                .required(WaystoneComponent.of("waystone"))
                .handler(this::removeFavorite)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("sortingmode")
                .senderType(Player.class)
                .required("mode", IntegerParser.integerParser())
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
                .required("term", StringParser.stringParser())
                .handler(this::searchWayStoneTerm)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("edit")
                .senderType(Player.class)
                .required(WaystoneComponent.of("waystone"))
                .handler(this::editWayStone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("create")
                .senderType(Player.class)
                .permission("waystones.admin")
                .required("location", LocationParser.locationParser())
                .required("name", StringParser.stringParser())
                .handler(this::createWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("remove")
                .permission("waystones.admin")
                .required(WaystoneComponent.of("waystone"))
                .handler(this::removeWaystone)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .required(WaystoneComponent.of("waystone"))
                .literal("add")
                .required("player", PlayerParser.playerParser())
                .handler(this::addPlayerToAccessList)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .required(WaystoneComponent.of("waystone"))
                .literal("remove")
                .required("player", PlayerParser.playerParser())
                .handler(this::removePlayerFromAccessList)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("access")
                .required(WaystoneComponent.of("waystone"))
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
                .required(WaystoneComponent.of("waystone"))
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
                .required("name", StringParser.stringParser())
                .required("public", BooleanParser.booleanParser())
                .permission("waystones.category")
                .handler(this::addCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("remove")
                .required("name", StringParser.stringParser())
                .permission("waystones.category")
                .handler(this::removeCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("list")
                .permission("waystones.category")
                .handler(this::listCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("category")
                .literal("set")
                .required(WaystoneComponent.of("waystone"))
                .required("category", IntegerParser.integerParser())
                .required("creation", BooleanParser.booleanParser())
                .handler(this::setCategory)
        );
        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("internal")
                .literal("button")
                .literal("access")
                .literal("add")
                .required(WaystoneComponent.of("waystone"))
                .senderType(Player.class)
                .handler(this::buttonAddAccess)
        );


        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("popularity")
                .literal("decrease")
                .permission("waystones.admin")
                .handler(this::decreasePopularity)
        );

        commandManager.command(commandManager.commandBuilder("waystone", "waystones")
                .literal("trader")
                .literal("mark")
                .required("entity", SingleEntitySelectorParser.singleEntitySelectorParser())
                .permission("waystones.trader.mark")
                .handler(this::markTrader)
        );
    }

    private void markTrader(CommandContext<CommandSender> context) {
        SingleEntitySelector entitySelector = context.get("entity");

        Entity entity = entitySelector.single();
        entity.getPersistentDataContainer().set(new NamespacedKey("waystones", "trader"), PersistentDataType.BYTE, (byte) 1);
    }

    private void decreasePopularity(CommandContext<CommandSender> context) {
        manager.decreaseGlobalUsesScore();
        context.sender().sendMessage(Component.translatable("waystones.popularity.decrease"));
    }

    private void forceMode(CommandContext<Player> context) {
        Player player = context.sender();
        if (manager.toggleForceMode(player)) {
            player.sendMessage(Component.translatable("waystones.force.on", TextUtil.GREEN));
        } else player.sendMessage(Component.translatable("waystones.force.off", TextUtil.GREEN));
    }

    private void menu(CommandContext<Player> context) {
        screen.menu(context.sender(), null);
    }

    private void browseCategorySelect(CommandContext<Player> context) {
        screen.browseCategorySelection(context.sender());
    }

    private void browseCategory(CommandContext<Player> context) {
        screen.browse(context.sender(), manager.getCategory((int) context.get("category")));
    }

    private void teleportToWayStone(CommandContext<Player> context) {
        Player player = context.sender();
        StoredWaystone waystone = context.get("waystone");

        boolean teleportAllowed = manager.forceMode(player);
        boolean xpNeeded = false;

        for (StoredWaystone w : manager.getWaystones(player.getWorld())) {
            if (w.location().distance(player.getLocation()) <= 5) teleportAllowed = true;
        }

        if (!player.getWorld().getUID().equals(waystone.world())) {
            player.sendMessage(Component.translatable("waystones.teleport.differentworld", TextUtil.ORANGE));
            return;
        }

        if (!teleportAllowed) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null) {
                    if (stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "portable"))) {
                        if (player.getLevel() >= 1) {
                            xpNeeded = true;
                            teleportAllowed = true;
                        } else {
                            player.sendMessage(Component.translatable("waystones.teleport.noxp", TextUtil.ORANGE));
                            return;
                        }
                        break;
                    }
                }
            }
        }

        if (!teleportAllowed) {
            player.sendMessage(Component.translatable("waystones.teleport.nowaystone", TextUtil.ORANGE));
            return;
        }

        if (manager.canTeleport(waystone, player)) {
            Location location = waystone.location();
            Location safeLocation = null;

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = location.clone().add(x, 0, z).getBlock();
                    Block upperBlock = location.clone().add(x, 1, z).getBlock();
                    Block lowerBlock = location.clone().add(x, -1, z).getBlock();
                    if (block.isEmpty() && upperBlock.isEmpty() && lowerBlock.isSolid()) {
                        safeLocation = block.getLocation().toCenterLocation();
                        break;
                    }
                }
                if (safeLocation != null) {
                    break;
                }
            }

            if (safeLocation == null) {
                player.sendMessage(Component.translatable("waystones.teleport.nospace", TextUtil.ORANGE));
                return;
            }

            if (xpNeeded) player.giveExpLevels(-1);

            player.teleportAsync(safeLocation);
            manager.addTeleport(player, waystone.id());
        }
    }

    private void editWayStone(CommandContext<Player> context) {
        Player player = context.sender();
        StoredWaystone waystone = context.get("waystone");
        if (waystone.owner().equals(player.getUniqueId()) || manager.forceMode(player)) {
            screen.settings(player, waystone);
        }
    }

    private void searchWayStone(CommandContext<Player> context) {
        screen.search(context.sender());
    }

    private void searchWayStoneTerm(CommandContext<Player> context) {
        Player player = context.sender();
        screen.list(player, context.get("term"));
    }

    private void addFavorite(CommandContext<Player> context) {
        Player player = context.sender();
        StoredWaystone waystone = context.get("waystone");
        manager.addFavorite(player, waystone.id());
        screen.menu(player, null);
    }

    private void removeFavorite(CommandContext<Player> context) {
        Player player = context.sender();
        StoredWaystone waystone = context.get("waystone");
        manager.removeFavorite(player, waystone.id());
        screen.menu(player, null);
    }

    private void sortingMode(CommandContext<Player> context) {
        Player player = context.sender();
        manager.setSortMode(player, SortMode.valueByNumber(context.get("mode")));
        screen.menu(player, null);
    }

    private void listWaystones(CommandContext<Player> context) {
        Player player = context.sender();
        World world = context.getOrDefault("world", player.getWorld());

        player.sendMessage("Waystones in " + world.getName() + ":");

        List<StoredWaystone> waystones = manager.getWaystones(world.getUID());
        for (StoredWaystone waystone : waystones) {
            TagResolver.Single id = Placeholder.parsed("id", waystone.id().toString());
            TagResolver.Single name = Placeholder.parsed("name", waystone.name());
            TagResolver.Single owner = Placeholder.parsed("owner", Objects.requireNonNullElse(plugin.getServer().getOfflinePlayer(waystone.owner()).getName(), "?"));
            TagResolver.Single owner_uuid = Placeholder.parsed("owner_uuid", waystone.owner().toString());

            TagResolver.Single x = Placeholder.parsed("waystone_x", String.valueOf(waystone.block_x()));
            TagResolver.Single y = Placeholder.parsed("waystone_y", String.valueOf(waystone.block_y()));
            TagResolver.Single z = Placeholder.parsed("waystone_z", String.valueOf(waystone.block_z()));

            player.sendMessage(MiniMessage.miniMessage().deserialize("• <hover:show_text:'ID: <id><br>Click to copy'><click:copy_to_clipboard:\"<id>\"><name></click></hover> (<hover:show_text:'<owner_uuid><br>Click to copy'><click:copy_to_clipboard:\"<owner_uuid>\">Owner: <owner></click></hover>) <hover:show_text:'Click to teleport'><click:run_command:\"/tp <waystone_x> <waystone_y> <waystone_z>\"><green>[<waystone_x>, <waystone_y>, <waystone_z>]</green></click></hover>", id, name, owner, owner_uuid, x, y, z));
        }
    }

    private void giveWaystone(CommandContext<Player> context) {
        Player player = context.sender();
        player.getInventory().addItem(plugin.getStatic());
    }

    private void givePortableWaystone(CommandContext<Player> context) {
        Player player = context.sender();
        player.getInventory().addItem(plugin.getPortable());
    }

    private void createWaystone(CommandContext<Player> context) {
        Player player = context.sender();
        Location location = context.get("location");
        String name = context.get("name");

        if (manager.isNameUsed(name)) {
            player.sendMessage(Component.translatable("waystones.ui.name.taken", TextColor.color(255, 73, 0), Component.text(name)));
            return;
        }

        StoredWaystone waystone = manager.createWaystone(name, player.getUniqueId(), Visibility.PUBLIC.id(), -1, location);

        display.updateDisplay(waystone);
        player.sendMessage(Component.translatable("waystones.ui.create", TextUtil.GREEN, Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()), Component.text(name), Component.text(waystone.id().toString())));
    }


    private void removeWaystone(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.remove")) return;

            if (!(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.translatable("waystones.remove.inventory_full", TextUtil.RED));
                    return;
                }
                player.getInventory().addItem(plugin.getStatic());
            }
        }

        display.clearDisplay(waystone);
        manager.removeWaystone(waystone.id());

        sender.sendMessage(Component.translatable("waystones.remove", TextColor.color(255, 73, 0), Component.text(waystone.id().toString())));
    }


    private void addPlayerToAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();

        StoredWaystone waystone = context.get("waystone");
        Player target = context.get("player");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.admin")) return;
        }

        manager.addAccess(target, waystone.id());
        sender.sendMessage(Component.translatable("waystones.access.add", TextUtil.GREEN, target.displayName(), Component.text(waystone.id().toString())));
    }

    private void removePlayerFromAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();

        StoredWaystone waystone = context.get("waystone");
        Player target = context.get("player");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !player.hasPermission("waystones.admin")) return;
        }

        manager.removeAccess(target, waystone.id());
        sender.sendMessage(Component.translatable("waystones.access.remove", TextUtil.GREEN, target.displayName(), Component.text(waystone.name())));
    }

    private void showAccessList(CommandContext<CommandSender> context) {
        CommandSender sender = context.sender();

        StoredWaystone waystone = context.get("waystone");

        if (sender instanceof Player player) {
            if (!waystone.owner().equals(player.getUniqueId()) && !manager.forceMode(player)) return;
        }

        List<OfflinePlayer> list = manager.getAccess(waystone.id());

        if (!list.isEmpty()) sender.sendMessage(Component.translatable("waystones.access.list", TextUtil.GREEN));
        else sender.sendMessage(Component.translatable("waystones.access.list.empty", TextUtil.GREEN));

        for (OfflinePlayer p : list) {
            sender.sendMessage(Component.text(Objects.requireNonNullElse(p.getName(), p.getUniqueId().toString())).hoverEvent(HoverEvent.showText(Component.text(p.getUniqueId().toString()))));
        }

    }

    private void reloadDisplay(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        display.updateDisplay(waystone);
        context.sender().sendMessage(Component.translatable("waystones.display.reload.waystone", TextUtil.GREEN, Component.text(waystone.id().toString())));
    }

    private void reloadAllDisplays(CommandContext<CommandSender> context) {
        display.updateAll();
        context.sender().sendMessage(Component.translatable("waystones.display.reload.all", TextUtil.GREEN));
    }

    private void clearDisplays(CommandContext<CommandSender> context) {
        display.removeAll();
        context.sender().sendMessage(Component.translatable("waystones.display.clear", TextUtil.GREEN));
    }


    private void addCategory(CommandContext<CommandSender> context) {
        if (manager.addCategory(context.get("name"), context.get("public")) != null) {
            String type = "restricted";
            if (context.get("public")) type = "public";
            context.sender().sendMessage(Component.translatable("waystones.category.add." + type, TextUtil.GREEN, Component.text(context.get("name").toString()).color(TextColor.color(255, 255, 255))));
        } else {
            context.sender().sendMessage(Component.translatable("waystones.category.nametaken", Component.text(context.get("name").toString())));
        }
    }

    private void removeCategory(CommandContext<CommandSender> context) {
        manager.removeCategory(context.get("name"));
        context.sender().sendMessage(Component.translatable("waystones.category.remove", TextUtil.GREEN, Component.text(context.get("name").toString()).color(TextColor.color(255, 255, 255))));
    }

    private void listCategory(CommandContext<CommandSender> context) {
        manager.getCategories().forEach(c -> context.sender().sendMessage(Component.text(c.name()).color(TextUtil.GREEN).append(Component.text(" - " + c.isPublic()).color(TextUtil.WHITE))));
    }

    private void setCategory(CommandContext<CommandSender> context) {
        StoredWaystone waystone = context.get("waystone");
        CommandSender source = context.sender();
        if (manager.canEdit(waystone, source)) {
            Category category = manager.getCategory((int) context.get("category"));
            if (category == null) {
                source.sendMessage(Component.translatable("waystones.category.invalid"));
                return;
            }
            manager.updateWaystone(new StoredWaystone(waystone.id(), waystone.name(), waystone.owner(), waystone.visibility(), category, waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
            source.sendMessage(Component.translatable("waystones.category.set", TextUtil.GREEN, Component.text(waystone.name()), Component.text(category.name())));
            if (context.get("creation")) {
                screen.accessSettings((Player) context.sender(), waystone);
            }
            return;
        }
        source.sendMessage(Component.translatable("waystones.permission.edit", TextUtil.RED, Component.text(context.get("name").toString()).color(TextUtil.WHITE)));
    }

    private void buttonAddAccess(CommandContext<Player> context) {
        StoredWaystone waystone = context.get("waystone");
        screen.addAccess(context.sender(), waystone);
    }

}
