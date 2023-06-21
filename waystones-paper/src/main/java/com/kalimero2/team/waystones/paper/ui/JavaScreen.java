package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.PlayerData;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaScreen {

    private PaperWayStones plugin;

    public JavaScreen(PaperWayStones plugin) {
        this.plugin = plugin;
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
            owned = clickedwaystone.owner().equals(player.getUniqueId());
        }
        if (owned) current_page = current_page.append(Component.text(" [ \uD83D\uDD89 ] ").hoverEvent(HoverEvent.showText(Component.text("Waystone bearbeiten"))).clickEvent(ClickEvent.runCommand("/waystone edit " + clickedwaystone.id())).append(Component.text("    [ \uD83D\uDD0D Suchen ]").hoverEvent(HoverEvent.showText(Component.text("Suchen"))).clickEvent(ClickEvent.runCommand("/waystone search"))).color(TextColor.color(0, 10, 200)));
        else current_page = current_page.append(Component.text("    [  \uD83D\uDD0D  Suchen  ]   ").color(TextColor.color(0, 10, 200)).hoverEvent(HoverEvent.showText(Component.text("Suchen"))).clickEvent(ClickEvent.runCommand("/waystone search")));
        current_page = current_page.append(Component.newline());
        current_page = current_page.append(Component.newline());


        PlayerData playerData = new PlayerData(player);


        Storage storage = plugin.getStorage();

        StoredWaystone[] allWaystones = storage.getWaystones(player.getWorld().getUID(), playerData.sortMode());
        List<StoredWaystone> nonFavorites = new ArrayList<>(Arrays.stream(allWaystones).toList());

        List<StoredWaystone> waystones = new ArrayList<>();

        for (StoredWaystone waystone : allWaystones) {
            if (Arrays.stream(playerData.favorites()).boxed().toList().contains(waystone.id())) {
                waystones.add(waystone);
                nonFavorites.remove(waystone);
            }
        }

        waystones.addAll(nonFavorites);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 12) {
                current_page = current_page.append(sortBar(playerData.sortMode()));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (Arrays.stream(playerData.favorites()).boxed().toList().contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 220, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone " + "favorite " + action + " " + waystone.id())));
            current_page = current_page.append(Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren").append(Component.newline()).append(Component.text("Waystone ID: " + waystone.id()).decorate(TextDecoration.BOLD)))));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        if (counter < 12) {

            for (int i = 0; i < 12-counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            current_page = current_page.append(sortBar(playerData.sortMode()));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void list(Player player, String search) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player.getWorld().getUID(), search);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 14) {
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            current_page = current_page.append(Component.text("• " + waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren"))));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }
}
