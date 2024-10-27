package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JavaScreens {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    private final Component anvilUIPrefix = MiniMessage.miniMessage().deserialize("<white><tr:space.-60><font:klm2:waystones>c</font><tr:space.-172>");

    private final Map<UUID, CacheEntry> pageCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000; // Cache size is 5 Minutes

    public JavaScreens(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager();
    }

    private static class CacheEntry {
        List<Component> pages;
        long timestamp;

        CacheEntry(List<Component> pages, long timestamp) {
            this.pages = pages;
            this.timestamp = timestamp;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
    }

    private List<Component> getCachedPages(Player player, StoredWaystone originWaystone, Collection<StoredWaystone> waystones, boolean showControls) {
        UUID cacheKey = getCacheKey(player, waystones, showControls);

        pageCache.entrySet().removeIf(entry -> entry.getValue().isExpired());

        CacheEntry entry = pageCache.get(cacheKey);

        if (entry != null && !entry.isExpired()) {
            return entry.pages;
        }

        List<Component> pages = generateWaystonePages(player, originWaystone, waystones, showControls);
        pageCache.put(cacheKey, new CacheEntry(pages, System.currentTimeMillis()));
        return pages;
    }

    private UUID getCacheKey(Player player, Collection<StoredWaystone> waystones, boolean showControls) {
        int waystonesHash = waystones.hashCode();
        String keyString = player.getUniqueId().toString() + waystonesHash + showControls;
        return UUID.nameUUIDFromBytes(keyString.getBytes());
    }

    private Component sortBar(SortMode mode) {
        TextColor color = TextColor.color(0, 0, 0);
        TextColor colorSelected = TextColor.color(255, 150, 0);
        TextColor colorSelectedInverted = TextColor.color(0, 190, 180);

        Component current_page = Component.newline();

        if (mode == SortMode.ALPHABETICAL)
            current_page = current_page.append(Component.text("    [A-Z]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 1")));
        else if (mode == SortMode.ALPHABETICAL_DESCENDING)
            current_page = current_page.append(Component.text("    [A-Z]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 0")));
        else
            current_page = current_page.append(Component.text("    [A-Z]").color(color).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 0")));

        if (mode == SortMode.POPULARITY)
            current_page = current_page.append(Component.text("    [★★★]").color(colorSelected).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 3")));
        else if (mode == SortMode.POPULARITY_ASCENDING)
            current_page = current_page.append(Component.text("    [★★★]").color(colorSelectedInverted).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 2")));
        else
            current_page = current_page.append(Component.text("    [★★★]").color(color).clickEvent(ClickEvent.runCommand("/waystone internal sortingmode 2")));

        return current_page;
    }


    public void menu(Player player, @Nullable StoredWaystone waystone) {
        // When the Player opens the Waystone Menu, we check if the Waystone is visible to them (unlisted are not shown)
        List<StoredWaystone> waystones = manager.getWaystones(player, player.getWorld().getUID()).stream().filter(w -> manager.canSee(w, player)).toList();
        List<Component> pages = getCachedPages(player, waystone, waystones, true);
        player.openBook(Book.book(Component.empty(), Component.empty(), pages));
    }


    public void browse(Player player, Category category) {
        // When the Player opens the Waystone Menu, we check if the Waystone is visible to them (unlisted are not shown)
        List<StoredWaystone> waystones = manager.getWaystones(player.getWorld(), category).stream().filter(w -> manager.canSee(w, player)).toList();
        List<Component> pages = getCachedPages(player, null, waystones, false);
        player.openBook(Book.book(Component.empty(), Component.empty(), pages));
    }

    private List<Component> generateWaystonePages(Player player, @Nullable StoredWaystone originWaystone, Collection<StoredWaystone> waystones, boolean showControls) {
        List<Component> pages = new ArrayList<>();
        int counter = 0;
        Component current_page = Component.empty();

        if (showControls) {
            current_page = current_page.append(Component.text(" [ ◇ ]").color(TextColor.color(0, 10, 200)).hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.browse"))).clickEvent(ClickEvent.runCommand("/waystone menu category")).append(Component.text("  [  \uD83D\uDD0D  ").append(Component.translatable("waystones.ui.search")).append(Component.text("  ]")).hoverEvent(HoverEvent.showText(Component.translatable("waystones.ui.search"))).clickEvent(ClickEvent.runCommand("/waystone search"))));
            current_page = current_page.append(Component.newline());
            current_page = current_page.append(Component.newline());
            counter = 1;
        }


        for (StoredWaystone waystone : waystones) {
            counter++;
            if (counter == 12) {
                if (showControls) current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }

            String action = "add";

            TextColor waystoneColor = NamedTextColor.BLACK;

            if (plugin.getManager().getFavorites(player).contains(waystone.id())) {
                action = "remove";
                waystoneColor = NamedTextColor.YELLOW;
            }

            current_page = current_page.append(Component.text("[★] ").color(waystoneColor).clickEvent(ClickEvent.runCommand("/waystone internal " + "favorite " + action + " " + waystone.id())));

            waystoneColor = NamedTextColor.BLACK;

            if (originWaystone != null){
                if (waystone.id().equals(originWaystone.id())){
                    waystoneColor = NamedTextColor.GREEN;
                }
            }

            Component hoverText = Component.translatable("waystones.ui.clicktoteleport");
            hoverText = hoverText.append(Component.newline()).append(Component.newline());
            hoverText = hoverText.append(Component.text(waystone.category().name()));

            if (player.hasPermission("waystones.hover_details")) {
                hoverText = hoverText.append(Component.newline()).append(Component.newline());
                hoverText = hoverText.append(Component.text("ID: " + waystone.id()));
                hoverText = hoverText.append(Component.newline());
                hoverText = hoverText.append(Component.text("Owner: " + Bukkit.getOfflinePlayer(waystone.owner()).getName()));
                hoverText = hoverText.append(Component.newline()).append(Component.newline());
                hoverText = hoverText.append(Component.text("Score: " + waystone.uses()));
                hoverText = hoverText.append(Component.newline());
                hoverText = hoverText.append(Component.text("Visibility: " + waystone.visibility()));
                hoverText = hoverText.append(Component.newline());
                hoverText = hoverText.append(Component.text("Pos: [" + waystone.block_x() + ", " + waystone.block_y() + ", " + waystone.block_z() + "]"));
            }

            Component waystoneEntry = Component.text(waystone.name()).clickEvent(ClickEvent.runCommand("/waystone tp " + waystone.id())).color(waystoneColor).hoverEvent(HoverEvent.showText(hoverText));

            current_page = current_page.append(waystoneEntry);
            current_page = current_page.append(Component.newline());
        }

        if (counter < 12) {
            for (int i = 0; i < 12 - counter; i++) {
                current_page = current_page.append(Component.newline());
            }
             if (showControls) current_page = current_page.append(sortBar(plugin.getManager().getSortMode(player)));
        }

        pages.add(current_page);
        return pages;
    }

    public void search(Player player, @Nullable String searchTerm) {
        Component title = anvilUIPrefix.append(Component.text(searchTerm == null ? "Name des Waystones oder Teile des Namen" : "Es gibt keinen Waystone dessen Name '" + searchTerm + "' enthält."));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        if (searchTerm == null) searchTerm = "Suchbegriff";

        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(searchTerm));
        item.setItemMeta(meta);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(item).itemOutput(item).onClick((n, state) -> {
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
            } else {
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
        // When the Player searches for a Waystone, we only check if they can teleport (unlisted are shown)
        List<StoredWaystone> waystones = manager.getWaystones(player.getWorld().getUID(), search).stream().filter(w -> manager.canTeleport(w, player)).toList();
        List<Component> pages = getCachedPages(player, null, waystones, false);
        player.openBook(Book.book(Component.empty(), Component.empty(), pages));
    }

    public void addAccess(Player player, StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("name"));
        stack.setItemMeta(meta);

        Component title = anvilUIPrefix.append(Component.translatable("waystones.ui.anvil.player"));
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(stack).onClick((n, state) -> {
            if(n == 0 || n == 1){
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            }

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
        }).plugin(plugin).open(player);

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

    public void categorySelection(Player player, @NotNull StoredWaystone waystone, boolean creation) {
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
            current_page = current_page.append(Component.text(category.name()).clickEvent(ClickEvent.runCommand("/waystone category set " + waystone.id() + " " + category.id() + " " + creation)));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));

    }

    public void browseCategorySelection(Player player) {
        List<Component> pages = new ArrayList<>();
        Component current_page = Component.translatable("waystones.ui.category.description").decorate(TextDecoration.BOLD);
        current_page = current_page.append(Component.newline().decoration(TextDecoration.BOLD, false));
        current_page = current_page.append(Component.newline());
        int counter = 3;

        WaystoneManager manager = plugin.getManager();

        for (Category category : manager.getCategories()) {

            counter++;
            if (counter == 14) {
                pages.add(current_page);
                current_page = Component.empty();
                counter = 0;
            }
            current_page = current_page.append(Component.text(category.name()).clickEvent(ClickEvent.runCommand("/waystone menu category " + category.id())));
            current_page = current_page.append(Component.newline());

            player.openBook(Book.book(Component.empty(), Component.empty(), pages));
        }
        pages.add(current_page);

        player.openBook(Book.book(Component.empty(), Component.empty(), pages));
    }
}
