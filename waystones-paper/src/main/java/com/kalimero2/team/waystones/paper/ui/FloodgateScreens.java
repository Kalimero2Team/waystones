package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FloodgateScreens {
    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    public FloodgateScreens(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager();
    }

    public void menu(Player player) {
        CustomForm.Builder builder = CustomForm.builder().title("Waystones").label("Wähle einen Waystone aus!");

        builder.input("Suchen", "Waystone Namen hier eingeben", "");

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.option("Alphabetisch");
        dropdownBuilder.option("Alphabetisch invertiert");
        dropdownBuilder.option("Beliebtheit");
        dropdownBuilder.option("Beliebtheit invertiert");
        dropdownBuilder.defaultOption(manager.getSortMode(player).ordinal());
        builder.dropdown(dropdownBuilder);

        DropdownComponent.Builder dropdownBuilder2 = DropdownComponent.builder();
        dropdownBuilder2.option("Alle");

        for (Category category : manager.getCategories()) {
            dropdownBuilder2.option(category.name());
        }
        dropdownBuilder2.defaultOption(0);
        builder.dropdown(dropdownBuilder2);

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput();
            manager.setSortMode(player, SortMode.valueByNumber(customFormResponse.asDropdown(2)));
            int c = customFormResponse.asDropdown(3);
            Category category = c == 0 ? Category.NONE : manager.getCategories().stream().toList().get(c-1);
            list(player, input, category);
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    public void list(Player player, String search, Category category) {
        SimpleForm.Builder builder = SimpleForm.builder().title("Waystones").content("Wähle einen Waystone aus!");

        List<StoredWaystone> allWaystones = plugin.getManager().getWaystones(player.getWorld().getUID(), search);
        allWaystones = allWaystones.stream().filter(waystone -> waystone.category().equalsOrUndefined(category)).toList();
        final List<StoredWaystone> waystones;
        if (search == null || search.isEmpty() || search.isBlank()){
            waystones = allWaystones.stream().filter(waystone -> manager.canSee(waystone, player)).toList();
        } else {
            waystones = allWaystones.stream().filter(waystone -> manager.canTeleport(waystone, player)).toList();
        }

        if (waystones.isEmpty()) {
            builder.content("Es konnten keine Waystones gefunden werden, dessen Name '" + search + "' enthält.");
        }

        for (StoredWaystone waystone : waystones) {
            builder.button(waystone.name());
        }

        builder.validResultHandler(simpleFormResponse -> {
            int clickedButtonId = simpleFormResponse.clickedButtonId();
            StoredWaystone waystone = waystones.get(clickedButtonId);
            player.chat("/waystone tp " + waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }


    /**
     * Opens the Waystone rename menu for the player
     *
     * @param player The player that wants to rename the waystone
     * @param lcr    Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void setCategory(Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr, boolean creation) {
        if (!manager.canEdit(waystone, player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.name());

        switch (lcr) {
            case NAME_TAKEN -> {
                builder.label("Dieser Name ist bereits vergeben! Bitte wähle einen anderen Namen.");
            }
            case CATEGORY_PRIVATE -> {
                builder.label("Diese Kategorie ist nur für Teammitglieder verfügbar! Bitte wähle eine andere Kategorie.");
            }
            case CATEGORY_INVALID -> {
                builder.label("Diese Kategorie existiert nicht! Bitte wähle eine andere Kategorie.");
            }
            case PLAYER_INVALID -> {
                builder.label("Dieser Spieler existiert nicht.");
            }
            default -> {
                builder.label("Setze die Kategorie deines Waystones");
            }
        }

        DropdownComponent.Builder dropdownBuilder2 = DropdownComponent.builder();
        dropdownBuilder2.text("Kategorie");

        List<Category> list = new ArrayList<>();
        for (Category c : manager.getCategories()) {
            dropdownBuilder2.option(c.name());
            list.add(c);
        }
        int defaultOption = list.indexOf(waystone.category());
        if (defaultOption == -1) defaultOption = 0;
        dropdownBuilder2.defaultOption(defaultOption);
        builder.dropdown(dropdownBuilder2);

        builder.validResultHandler(customFormResponse -> {
            int category = customFormResponse.asDropdown() + 1;
            Category storedCategory = manager.getCategory(category);
            if (storedCategory == null) {
                return;
            }
            if (!storedCategory.isPublic() && !manager.forceMode(player) && !player.hasPermission("waystones.category")) {
                return;
            }

            manager.updateWaystone(new StoredWaystone(waystone.id(), waystone.name(), waystone.owner(), waystone.visibility(), storedCategory, waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
            accessView(player, waystone);
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }


    /**
     * Opens the access list of a waystone
     *
     * @param player   Player that wants to see the list
     * @param waystone Waystone the list is requested from
     */
    public void accessView(@NotNull Player player, @NotNull StoredWaystone waystone) {
        SimpleForm.Builder builder = SimpleForm.builder().title("Waystone " + waystone.name()).content("Zugriffsliste");

        for (OfflinePlayer p : manager.getAccess(waystone.id())) {
            builder.button(Objects.requireNonNullElse(p.getName(), p.getUniqueId().toString())); // TODO: Fetch name from Mojang API?
        }

        builder.validResultHandler(simpleFormResponse -> {
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    /**
     * Opens the menu to add a player to the access list
     *
     * @param player   Player that wants to edit the list
     * @param waystone Waystone the list should be changed of
     */
    public void accessAdd(@NotNull Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {
        if (!manager.canEdit(waystone, player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.name());

        switch (lcr) {
            case PLAYER_INVALID -> {
                builder.label("Dieser Spieler existiert nicht.");
            }
            case PLAYER_EXISTING -> {
                builder.label("Dieser Spieler ist bereits auf der Liste.");
            }
            default -> {
                builder.label("Spieler zur Zugriffsliste hinzufügen");
            }
        }

        builder.input("Spielername (Bei Bedrock mit . starten)");

        builder.validResultHandler(customFormResponse -> {
            OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(customFormResponse.asInput());
            if (p == null) {
                accessAdd(player, waystone, LastCreationResult.PLAYER_INVALID);
                return;
            }
            if (manager.hasAccess(p, waystone.id())) {
                accessAdd(player, waystone, LastCreationResult.PLAYER_EXISTING);
                return;
            }
            manager.addAccess(p, waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    /**
     * Opens the menu to remove a player from the accesslist
     *
     * @param player   Player that wants to edit the list
     * @param waystone Waystone the list should be changed of
     */
    public void accessRemove(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.name());

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.text("Spieler zum Entfernen");

        List<OfflinePlayer> list = new ArrayList<>();
        for (OfflinePlayer p : manager.getAccess(waystone.id())) {
            dropdownBuilder.option(p.getName());
            list.add(p);
        }
        builder.dropdown(dropdownBuilder);

        builder.validResultHandler(customFormResponse -> {
            if(list.isEmpty()) return;
            OfflinePlayer p = list.get(customFormResponse.asDropdown());
            manager.removeAccess(p, waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
