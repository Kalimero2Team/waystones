package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaScreens {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;
    private final DisplayManager displayManager;

    private final Component anvilUIPrefix = MiniMessage.miniMessage().deserialize("<white><tr:space.-60><font:klm2:waystones>c</font><tr:space.-172>");

    public JavaScreens(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
        this.displayManager = plugin.getDisplayManager();
    }


    private Component sortBar(SortMode mode) {
        TextColor color = TextColor.color(0, 0, 0);
        TextColor colorSelected = TextColor.color(255, 150, 0);
        TextColor colorSelectedInverted = TextColor.color(0, 190, 180);

        Component current_page = Component.newline();

        if (mode == SortMode.ALPHABETICAL)
            current_page = current_page.append(Component.text("  [A-Z]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 1")));
        else if (mode == SortMode.ALPHABETICAL_DESCENDING)
            current_page = current_page.append(Component.text("  [A-Z]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 0")));
        else
            current_page = current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 0")));

        if (mode == SortMode.NUMERIC)
            current_page = current_page.append(Component.text("  [1-2]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 3")));
        else if (mode == SortMode.NUMERIC_DESCENDING)
            current_page = current_page.append(Component.text("  [1-2]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 2")));
        else
            current_page = current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 2")));

        if (mode == SortMode.POPULARITY)
            current_page = current_page.append(Component.text("  [★★★]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 5")));
        else if (mode == SortMode.POPULARITY_ASCENDING)
            current_page = current_page.append(Component.text("  [★★★]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 4")));
        else
            current_page = current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 4")));

        return current_page;
    }


    public void menu(Player player, @Nullable StoredWaystone clickedwaystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 1;

        boolean owned = false;
        if (clickedwaystone != null) {
            owned = clickedwaystone.owner().equals(player.getUniqueId()) || manager.forceMode(player);
        }
        if (owned)
            current_page = current_page.append(Component.text(" [ \uD83D\uDD89 ] ").hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.edit")))).clickEvent(ClickEvent.runCommand("/waystone edit " + clickedwaystone.id())).append(Component.text("    [ \uD83D\uDD0D ").append(Component.translatable("waystones.ui.search")).append(Component.text(" ]")).hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.search"))).clickEvent(ClickEvent.runCommand("/waystone search"))).color(TextColor.color(0, 10, 200));
        else
            current_page = current_page.append(Component.text("    [  \uD83D\uDD0D  ").append(Component.translatable("waystones.ui.search")).append(Component.text("  ]   ")).color(TextColor.color(0, 10, 200)).hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.search"))).clickEvent(ClickEvent.runCommand("/waystone search")));

        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());

        WaystoneManager manager = plugin.getManager();

        List<StoredWaystone> waystones = manager.getWaystones(player, player.getWorld().getUID());

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 12) {
                current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (plugin.getManager().getFavorites(player).contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 220, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone internal " + "favorite " + action + " " + waystone.id())));

            color = TextColor.color(0, 0, 0);
            if (clickedwaystone != null) if (waystone.id() == clickedwaystone.id()) color = TextColor.color(0, 180, 50);

            Component hoverText = Component.translatable("waystones.ui.clicktoteleport");
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("ID: " + waystone.id()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text(waystone.category().name()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("Pos: [" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + "]"));


            current_page = current_page.append(Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).color(color).hoverEvent(HoverEvent.showText(hoverText)));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        if (counter < 12) {

            for (int i = 0; i < 12 - counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void search(Player player, @Nullable String searchTerm) {

        Component title = anvilUIPrefix.append(Component.text(searchTerm == null ? "Name des Waystones oder Teile des Namen" : "Es gibt keinen Waystone dessen Name '" + searchTerm + "' enthält."));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        if (searchTerm == null) searchTerm = "Suchbegriff";

        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(searchTerm));
        item.setItemMeta(meta);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemOutput(item).onClick((n, state) -> {
            if (state.getText().length() > 16) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Maximal 16 Zeichen!"));
            }
            List<StoredWaystone> waystones = manager.getWaystones(player.getWorld().getUID(), state.getText());
            if (waystones.isEmpty()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        search(player, state.getText());
                    }
                }.runTaskLater(plugin, 1);
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            }
            else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        list(player, state.getText());
                    }
                }.runTaskLater(plugin, 1);
            }
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).plugin(plugin).open(player);

    }


    public void list(Player player, String search) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 1;

        current_page = current_page.append(Component.translatable("waystones.ui.search.results",TextColor.color(0, 10, 200)).decorate(TextDecoration.BOLD));
        current_page = current_page.append(Component.newline().decoration(TextDecoration.BOLD, false));
        current_page = current_page.append(Component.newline());

        WaystoneManager manager = plugin.getManager();

        List<StoredWaystone> waystones = manager.getWaystones(player.getWorld().getUID(), search);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 12) {
                current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (plugin.getManager().getFavorites(player).contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 220, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone internal " + "favorite " + action + " " + waystone.id())));

            Component hoverText = Component.translatable("waystones.ui.clicktoteleport");
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("ID: " + waystone.id()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text(waystone.category().name()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("Pos: [" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + "]"));

            current_page = current_page.append(Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).color(color).hoverEvent(HoverEvent.showText(hoverText)));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        if (counter < 12) {

            for (int i = 0; i < 12 - counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }


    public void create(Player player, Location location, ItemStack stack) {
        Component title = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.name"));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);
        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(plugin.getStatic()).onClick((n, state) -> {
            if (state.getText().length() > 16) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Maximal 16 Zeichen!"));
            }
            if (manager.isNameUsed(state.getText())) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Name ist bereits vergeben!"));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (stack.getAmount() < 1) return;
                    plugin.getManager().createWaystone(state.getText(), player.getUniqueId(), 1, -1, location);
                    StoredWaystone waystone = plugin.getManager().getWaystone(location);
                    displayManager.updateDisplay(waystone);
                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        stack.setAmount(stack.getAmount() - 1);
                    }
                    visibilitySelection(player, waystone);
                }
            }.runTask(plugin);
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).preventClose().plugin(plugin).open(player);
    }


    public void addAccess(Player player, StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("name"));
        stack.setItemMeta(meta);

        Component title = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.player"));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(stack).onClick((n, state) -> {
            OfflinePlayer p = plugin.getServer().getOfflinePlayerIfCached(state.getText());
            if (p == null) {
                Component title2 = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.player.invalid"));
                String jsonTitle2 = JSONComponentSerializer.json().serialize(title2);
                return Collections.singletonList(AnvilGUI.ResponseAction.updateJsonTitle(jsonTitle2, true));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    manager.addAccess(p, waystone.id());
                }
            }.runTask(plugin);
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).preventClose().plugin(plugin).open(player);

    }

    public void accessSettings(Player player, StoredWaystone waystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 4;

        current_page = current_page.append(Component.translatable("waystones.ui.access.title").color(TextColor.color(0, 10, 200)).decorate(TextDecoration.BOLD));
        current_page = current_page.append(Component.newline().decoration(TextDecoration.BOLD, false));
        current_page = current_page.append(Component.translatable("waystones.ui.access.add").clickEvent(ClickEvent.runCommand("/waystone internal button access add " + waystone.id())));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());

        WaystoneManager manager = plugin.getManager();

        List<OfflinePlayer> list = manager.getAccess(waystone.id());

        for (OfflinePlayer p : list) {

            counter++;
            if (counter == 13) {
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String name = p.getName();
            current_page = current_page.append(Component.text("[X] ").color(TextUtil.RED).clickEvent(ClickEvent.runCommand("/waystone access " + waystone.id() + " remove " + name))).hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.access.remove")));
            current_page = current_page.append(Component.text(name));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }


    public void setOwner(Player player, StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        ItemStack stack = plugin.getStatic().clone();
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(waystone.name()));
        stack.setItemMeta(meta);

        Component title = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.player"));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(stack).onClick((n, state) -> {
            OfflinePlayer p = plugin.getServer().getOfflinePlayerIfCached(state.getText());
            if (p == null) {
                Component title2 = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.player.invalid"));
                String jsonTitle2 = JSONComponentSerializer.json().serialize(title2);
                return Collections.singletonList(AnvilGUI.ResponseAction.updateJsonTitle(jsonTitle2, true));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    StoredWaystone newWaystone = new StoredWaystone(waystone.id(), waystone.name(), p.getUniqueId(), waystone.visibility(), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses());
                    manager.updateWaystone(newWaystone);
                }
            }.runTask(plugin);
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).preventClose().plugin(plugin).open(player);

    }

    // TODO: Make this use a ButtonScreen
    public void visibilitySelection(Player player, StoredWaystone waystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();

        int id = waystone.id();

        current_page = current_page.append(Component.translatable("waystones.ui.visibility.title").decorate(TextDecoration.BOLD));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());

        current_page = current_page.append(Component.translatable("waystones.ui.visibility.public").clickEvent(ClickEvent.runCommand("/waystone internal creation visibility " + id + " public")));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.translatable("waystones.ui.visibility.unlisted").clickEvent(ClickEvent.runCommand("/waystone internal creation visibility " + id + " unlisted")));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.translatable("waystones.ui.visibility.private").clickEvent(ClickEvent.runCommand("/waystone internal creation visibility " + id + " private")));

        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.translatable("waystones.ui.visibility.info").decorate(TextDecoration.BOLD));

        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void categorySelection(Player player, @NotNull StoredWaystone waystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.translatable("waystones.ui.category.description").decorate(TextDecoration.BOLD);
        current_page = current_page.append(Component.newline().decoration(TextDecoration.BOLD, false));
        current_page = current_page.append(Component.newline());
        int counter = 3;

        WaystoneManager manager = plugin.getManager();

        for (Category category : manager.getCategories()) {

            if (!category.usableBy(player)) continue;

            counter++;
            if (counter == 14) {
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            current_page = current_page.append(Component.text(category.name()).clickEvent(ClickEvent.runCommand("/waystone category set " + waystone.id() + " " + category.id())));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));
    }
}
