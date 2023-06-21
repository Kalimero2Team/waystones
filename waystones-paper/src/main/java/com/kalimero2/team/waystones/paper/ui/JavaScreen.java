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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaScreen {

    private PaperWayStones plugin;

    public JavaScreen(PaperWayStones plugin) {
        this.plugin = plugin;
    }


    public void menu(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;


        PlayerData playerData = new PlayerData(player);
        SortMode mode = playerData.sortMode();



        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player.getWorld().getUID(), mode);

        for (StoredWaystone waystone : waystones) {
            if (waystone == null) continue;

            counter++;
            if (counter == 11) {
                TextColor color = TextColor.color(0, 0, 0);
                TextColor colorSelected = TextColor.color(255, 150, 0);
                TextColor colorSelectedInverted = TextColor.color(0, 190, 255);


                current_page = current_page.append(Component.newline());

                if (mode == SortMode.ALPHABETICAL) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelected));
                else if (mode == SortMode.ALPHABETICAL_DESCENDING) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelectedInverted));
                else current_page = current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 1")));

                if (mode == SortMode.NUMERIC) current_page = current_page.append(Component.text("  [1-2]").color(colorSelected));
                else if (mode == SortMode.NUMERIC_DESCENDING) current_page = current_page.append(Component.text("  [1-2]").color(colorSelectedInverted));
                else current_page = current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0")));

                if (mode == SortMode.POPULARITY) current_page = current_page.append(Component.text("  [★★★]").color(colorSelected));
                else if (mode == SortMode.POPULARITY_ASCENDING) current_page = current_page.append(Component.text("  [★★★]").color(colorSelectedInverted));
                else current_page = current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));


                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
                continue;
            }
            if (counter == 1) {
                current_page = current_page.append(Component.text(" [ \uD83D\uDD89 ]     [ \uD83D\uDD0D Suchen ]").color(TextColor.color(0, 10, 200)));
                current_page = current_page.append(Component.newline());
                current_page = current_page.append(Component.newline());
            }

            String action = "add";
            TextColor color = TextColor.color(0, 0, 0);
            if (Arrays.stream(playerData.favorites()).boxed().toList().contains(waystone.id())) {
                action = "remove";
                color = TextColor.color(255, 150, 0);
            }
            current_page = current_page.append(Component.text("[★] ").color(color).clickEvent(ClickEvent.runCommand("/waystone " + action + "favourite " + waystone.id())));
            current_page = current_page.append(Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).hoverEvent(HoverEvent.showText(Component.text("Klicke um zu diesem Waystone zu teleportieren").append(Component.newline()).append(Component.text("Waystone ID: " + waystone.id()).decorate(TextDecoration.BOLD)))));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }

        if (counter < 11) {

            for (int i = 0; i < 11-counter; i++) {
                current_page = current_page.append(Component.newline());
            }

            TextColor color = TextColor.color(0, 0, 0);
            TextColor colorSelected = TextColor.color(255, 150, 0);
            TextColor colorSelectedInverted = TextColor.color(0, 190, 255);

            if (mode == SortMode.ALPHABETICAL) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelected));
            else if (mode == SortMode.ALPHABETICAL_DESCENDING) current_page = current_page.append(Component.text("  [A-Z]").color(colorSelectedInverted));
            else current_page = current_page.append(Component.text("  [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 1")));

            if (mode == SortMode.NUMERIC) current_page = current_page.append(Component.text("  [1-2]").color(colorSelected));
            else if (mode == SortMode.NUMERIC_DESCENDING) current_page = current_page.append(Component.text("  [1-2]").color(colorSelectedInverted));
            else current_page = current_page.append(Component.text("  [1-2]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 0")));

            if (mode == SortMode.POPULARITY) current_page = current_page.append(Component.text("  [★★★]").color(colorSelected));
            else if (mode == SortMode.POPULARITY_ASCENDING) current_page = current_page.append(Component.text("  [★★★]").color(colorSelectedInverted));
            else current_page = current_page.append(Component.text("  [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone sortingmode 2")));
        }


        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void list(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.empty();
        int counter = 0;

        Storage storage = plugin.getStorage();

        StoredWaystone[] waystones = storage.getWaystones(player.getWorld().getUID());

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
