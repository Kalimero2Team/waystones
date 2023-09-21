package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaScreen {

    private final PaperWayStones plugin;
    private final Storage storage;
    private final DisplayManager displayManager;

    public JavaScreen(PaperWayStones plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
        this.displayManager = plugin.getDisplayManager();
    }


    private Component sortBar(SortMode mode) {
        TextColor color = TextColor.color(0, 0, 0);
        TextColor colorSelected = TextColor.color(255, 150, 0);
        TextColor colorSelectedInverted = TextColor.color(0, 190, 180);

        Component current_page = Component.newline();

        if (mode == SortMode.ALPHABETICAL) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone sortingmode 1")));
        else if (mode == SortMode.ALPHABETICAL_DESCENDING) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0")));
        else current_page = current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0")));

        if (mode == SortMode.NUMERIC) current_page = current_page.append(Component.text("  [1-2]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone sortingmode 3")));
        else if (mode == SortMode.NUMERIC_DESCENDING) current_page = current_page.append(Component.text("  [1-2]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));
        else current_page = current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));

        if (mode == SortMode.POPULARITY) current_page = current_page.append(Component.text("  [★★★]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone sortingmode 5")));
        else if (mode == SortMode.POPULARITY_ASCENDING) current_page = current_page.append(Component.text("  [★★★]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone sortingmode 4")));
        else current_page = current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 4")));

        return current_page;
    }


    public void menu(Player player, @Nullable StoredWaystone clickedwaystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 1;

        boolean owned = false;
        if (clickedwaystone!= null) {
            owned = clickedwaystone.owner().equals(player.getUniqueId()) || storage.forceMode(player);
        }
        if (owned) current_page = current_page.append(Component.text(" [ \uD83D\uDD89 ] ").hoverEvent(HoverEvent.showText(Component.text("Waystone bearbeiten"))).clickEvent(ClickEvent.runCommand("/waystone edit " + clickedwaystone.id())).append(Component.text("    [ \uD83D\uDD0D Suchen ]").hoverEvent(HoverEvent.showText(Component.text("Suchen"))).clickEvent(ClickEvent.runCommand("/waystone search"))).color(TextColor.color(0, 10, 200)));
        else current_page = current_page.append(Component.text("    [  \uD83D\uDD0D  Suchen  ]   ").color(TextColor.color(0, 10, 200)).hoverEvent(HoverEvent.showText(Component.text("Suchen"))).clickEvent(ClickEvent.runCommand("/waystone search")));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());

        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player.getWorld().getUID(), player);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 12) {
                current_page = current_page.append(sortBar(plugin.getStorage().getSortMode(player)));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (Arrays.stream(plugin.getStorage().getFavorites(player)).toList().contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 220, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone " + "favorite " + action + " " + waystone.id())));

            color = TextColor.color(0, 0, 0);
            if (clickedwaystone != null) if(waystone.id() == clickedwaystone.id()) color = TextColor.color(0, 180, 50);

            Component hoverText = Component.translatable("waystone.ui.clicktoteleport").fallback("Klicke um zu diesem Waystone zu teleportieren");
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

            for (int i = 0; i < 12-counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            current_page = current_page.append(sortBar(plugin.getStorage().getSortMode(player)));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void search(Player player, @Nullable String searchTerm) {

        String title = searchTerm == null ? "Name des Waystones oder Teile des Namen" : "Es gibt keinen Waystone dessen Name '"+searchTerm+"' enthält.";
        if (searchTerm == null) searchTerm = "Suchbegriff";

        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(searchTerm));
        item.setItemMeta(meta);

        new AnvilGUI.Builder().title(title).itemLeft(item).onClick((n, state) -> {
            if (state.getText().length() > 16) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Maximal 16 Zeichen!"));
            }
            StoredWaystone[] waystones = storage.getWaystones(player, state.getText());
            System.out.println(waystones.length);
            if (waystones.length == 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        search(player, state.getText());
                    }
                }.runTaskLater(plugin, 1);
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            }
            if (waystones.length == 1) player.chat("/waystone tp " + waystones[0].id());
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

        current_page = current_page.append(Component.text("Suchergebnisse").color(TextColor.color(0, 10, 200)).decorate(TextDecoration.BOLD));
        current_page = current_page.append(Component.newline().decoration(TextDecoration.BOLD, false));
        current_page = current_page.append(Component.newline());

        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player, search);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 12) {
                current_page = current_page.append(sortBar(plugin.getStorage().getSortMode(player)));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (Arrays.stream(plugin.getStorage().getFavorites(player)).toList().contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 220, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone " + "favorite " + action + " " + waystone.id())));

            Component hoverText = Component.translatable("waystone.ui.clicktoteleport").fallback("Klicke um zu diesem Waystone zu teleportieren");
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("ID: " + waystone.id()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text(waystone.category().name()));
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.newline());
            hoverText = hoverText.append(Component.text("Pos: [" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + "]"));

            current_page = current_page.append(Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).color(color).hoverEvent(HoverEvent.showText(hoverText)));            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        if (counter < 12) {

            for (int i = 0; i < 12-counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            current_page = current_page.append(sortBar(plugin.getStorage().getSortMode(player)));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }


    public void settings(Player player, StoredWaystone waystone) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();

        int id = waystone.id();

        current_page = current_page.append(Component.text(waystone.name()).decorate(TextDecoration.BOLD).color(TextColor.color(0, 100, 180)));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.text("ID: " + waystone.id()).color(TextColor.color(0, 100, 130)));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.text("Umbenennen").clickEvent(ClickEvent.runCommand("/waystone internal button rename " + id)));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.text("Entfernen").clickEvent(ClickEvent.runCommand("/waystone internal button remove " + id)));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.text("Eigentum übertragen").clickEvent(ClickEvent.runCommand("/waystone internal button transferownership " + id)));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());

        switch (waystone.visibility()) {
            case PUBLIC -> {
                current_page = current_page.append(Component.text("Privat stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " private")));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.text("Ungelistet stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " unlisted")));
            }
            case UNLISTED -> {
                current_page = current_page.append(Component.text("Öffentlich stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " public")));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.text("Privat stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " private")));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.text("Whitelist bearbeiten").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " edit")));
            }
            case PRIVATE -> {
                current_page = current_page.append(Component.text("Öffentlich stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " public")));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.text("Ungelistet stellen").clickEvent(ClickEvent.runCommand("/waystone visibility " + id + " unlisted")));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.text("Whitelist bearbeiten").clickEvent(ClickEvent.runCommand("/waystone access " + id + " edit")));
            }
            default ->
                current_page = current_page.append(Component.text("ERROR: Cannot resolve visibility code " + waystone.visibility()));
        }

        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void create(Player player, Location location, ItemStack stack) {

        new AnvilGUI.Builder().title("Gebe dem Waystone einen Namen").itemLeft(plugin.getItem()).onClick((n, state) -> {
            if (state.getText().length() > 16) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Maximal 16 Zeichen!"));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (stack.getAmount() < 1) return;
                    plugin.getStorage().addWaystone(state.getText(), player.getUniqueId(), 0, 1, location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
                    StoredWaystone waystone = plugin.getStorage().getWaystone(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID());
                    displayManager.updateDisplay(waystone);
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        stack.setAmount(stack.getAmount() - 1);
                    }
                    visibilitySelection(player, waystone);
                }
            }.runTask(plugin);
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).preventClose().plugin(plugin).open(player);

    }

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

        Storage storage = plugin.getStorage();

        for (Category category : storage.getCategories()) {

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
