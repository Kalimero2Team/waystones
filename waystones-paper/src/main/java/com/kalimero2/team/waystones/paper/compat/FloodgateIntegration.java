package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FloodgateIntegration {
    private final PaperWayStones plugin;
    private final Storage storage;

    public FloodgateIntegration(PaperWayStones plugins) {
        this.plugin = plugins;
        this.storage = plugin.getStorage();
    }

    public void menu(Player player) {

        CustomForm.Builder builder = CustomForm.builder().title("Waystones").label("Wähle einen Waystone aus!");

        builder.input("Suchen", "Waystone Namen hier eingeben", "");

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.option("Alphabetisch");
        dropdownBuilder.option("Alphabetisch invertiert");
        dropdownBuilder.option("Numerisch");
        dropdownBuilder.option("Numerisch invertiert");
        dropdownBuilder.option("Beliebtheit");
        dropdownBuilder.option("Beliebtheit invertiert");
        dropdownBuilder.defaultOption(storage.getSortMode(player).ordinal());
        builder.dropdown(dropdownBuilder);

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput();
            storage.setSortMode(player, SortMode.valueByNumber(customFormResponse.asDropdown()));
            list(player, input);
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    public void list(Player player, String search) {

        SimpleForm.Builder builder = SimpleForm.builder().title("Waystones").content("Wähle einen Waystone aus!");


        StoredWaystone[] waystones = plugin.getStorage().getWaystones(player, search);

        if (waystones.length == 0) {
            builder.content("Es konnten keine Waystones gefunden werden, dessen Name '" + search + "' enthält.");
        }

        for (StoredWaystone waystone : waystones) {
            builder.button(waystone.name());
        }

        builder.validResultHandler(simpleFormResponse -> {
            int clickedButtonId = simpleFormResponse.clickedButtonId();
            System.out.println(clickedButtonId);
            StoredWaystone waystone = waystones[clickedButtonId];
            player.chat("/waystone tp " + waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }


    /**
     * Opens the Waystone name selection menu for the player
     * @param player The player that placed the waystone
     * @param location The location where the player placed the waystone
     * @param stack The waystone item
     * @param lcr Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void create(Player player, Location location, ItemStack stack, LastCreationResult lcr) {

        CustomForm.Builder builder = CustomForm.builder().title("Waystones");

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
            default -> {
                builder.label("Erstelle einen Waystone");
            }
        }

        builder.input("Waystone Name", "Waystone Namen hier eingeben", "");

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.text("Sichtbarkeit");
        dropdownBuilder.option("Öffentlich");
        dropdownBuilder.option("Ungelistet");
        dropdownBuilder.option("Privat");
        dropdownBuilder.defaultOption(0);
        builder.dropdown(dropdownBuilder);

        DropdownComponent.Builder dropdownBuilder2 = DropdownComponent.builder();
        dropdownBuilder2.text("Kategorie");
        List<Category> list = new ArrayList<>();
        for (Category c : storage.getCategories()) {
            dropdownBuilder2.option(c.name());
            list.add(c);
        }
        dropdownBuilder2.defaultOption(0);
        builder.dropdown(dropdownBuilder2);

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput(1);
            if (!storage.nameFree(input)) {
                create(player, location, stack, LastCreationResult.NAME_TAKEN);
                return;
            }
            int visibility = customFormResponse.asDropdown(2);
            int category = customFormResponse.asDropdown(3);
            Category storedCategory = list.get(category);
            if (storedCategory == null) {
                create(player, location, stack, LastCreationResult.CATEGORY_INVALID);
                return;
            }
            if (!storedCategory.isPublic() && !storage.forceMode(player) && !player.hasPermission("waystones.category")) {
                create(player, location, stack, LastCreationResult.CATEGORY_PRIVATE);
                return;
            }

            storage.addWaystone(input, player.getUniqueId(), visibility, category, location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
            stack.setAmount(stack.getAmount() - 1);
            player.sendMessage(Component.text(""));
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }


    /**
     * Opens the edit menu for the player
     * @param player Player that wants to edit the waystone
     * @param waystone Waystone to edit
     */
    public void settings(@NotNull Player player, @NotNull StoredWaystone waystone) {

        SimpleForm.Builder builder = SimpleForm.builder().title("Waystone " + waystone.id()).content("Waystone bearbeiten");

        builder.button("Umbenennen");
        builder.button("Kategorie ändern");
        builder.button("Sichtbarkeit ändern");
        builder.button("Zugriff verwalten");

        builder.validResultHandler(simpleFormResponse -> {
            switch (simpleFormResponse.clickedButtonId()) {
                case 0 -> rename(player, waystone, LastCreationResult.FIRST_CALL);
                case 1 -> setCategory(player, waystone, LastCreationResult.FIRST_CALL);
                case 2 -> setVisibility(player, waystone, LastCreationResult.FIRST_CALL);
                case 3 -> accessSettings(player, waystone);
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }



    /**
     * Opens the Waystone settings menu for the player
     * @param player The player that wants to edit the waystone
     * @param lcr Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void settingsFull(Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {

        if (!waystone.checkTeleport(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

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
                builder.label("Erstelle einen Waystone");
            }
        }

        builder.input("Waystone Name", "Waystone Namen hier eingeben", waystone.name());

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.text("Sichtbarkeit");
        dropdownBuilder.option("Öffentlich");
        dropdownBuilder.option("Ungelistet");
        dropdownBuilder.option("Privat");
        dropdownBuilder.defaultOption(waystone.visibility().ordinal());
        builder.dropdown(dropdownBuilder);

        DropdownComponent.Builder dropdownBuilder2 = DropdownComponent.builder();
        dropdownBuilder2.text("Kategorie");

        System.out.println("DEFAULT: " + waystone.category().name() + ": " + waystone.category().id());

        List<Category> list = new ArrayList<Category>();
        for (Category c : storage.getCategories()) {
            dropdownBuilder2.option(c.name());
            list.add(c);
            System.out.println("Adding category:   " + c.name() + ": " + c.id());
        }
        int defaultOption = list.indexOf(waystone.category());
        System.out.println(defaultOption);
        if(defaultOption == -1) defaultOption = 0;
        dropdownBuilder2.defaultOption(defaultOption);
        builder.dropdown(dropdownBuilder2);

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput(1);
            if (input == null) {
                settingsFull(player, waystone, LastCreationResult.NAME_TAKEN);
                return;
            }
            if (!storage.nameFree(input) && !input.equalsIgnoreCase(waystone.name())) {
                settingsFull(player, waystone, LastCreationResult.NAME_TAKEN);
                return;
            }
            int visibility = customFormResponse.asDropdown(2);
            int category = customFormResponse.asDropdown(3);
            Category storedCategory = storage.getCategory(category);
            if (storedCategory == null) {
                settingsFull(player, waystone, LastCreationResult.CATEGORY_INVALID);
                return;
            }
            if (!storedCategory.isPublic() && !storage.forceMode(player) && !player.hasPermission("waystones.category")) {
                settingsFull(player, waystone, LastCreationResult.CATEGORY_PRIVATE);
                return;
            }

            storage.updateWaystone(new StoredWaystone(waystone.id(), input, waystone.owner(), Visibility.valueByNumber(visibility), storedCategory, waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }

    /**
     * Opens the Waystone rename menu for the player
     * @param player The player that wants to rename the waystone
     * @param lcr Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void rename(Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {

        if (!waystone.checkTeleport(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

        switch (lcr) {
            case NAME_TAKEN -> {
                builder.label("Dieser Name ist bereits vergeben! Bitte wähle einen anderen Namen.");
            }
            default -> {
                builder.label("Nenne den Waystone um");
            }
        }

        builder.input("Waystone Name", "Waystone Namen hier eingeben", waystone.name());

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput(1);
            if (!storage.nameFree(input)) {
                rename(player, waystone, LastCreationResult.NAME_TAKEN);
                return;
            }

            storage.updateWaystone(new StoredWaystone(waystone.id(), input, waystone.owner(), waystone.visibility(), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }


    /**
     * Opens the Waystone rename menu for the player
     * @param player The player that wants to rename the waystone
     * @param lcr Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void setVisibility(Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {

        if (!waystone.checkTeleport(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

        switch (lcr) {
            default -> {
                builder.label("Bearbeite die Sichtbarkeit des Waystones");
            }
        }

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.text("Sichtbarkeit");
        dropdownBuilder.option("Öffentlich");
        dropdownBuilder.option("Ungelistet");
        dropdownBuilder.option("Privat");
        dropdownBuilder.defaultOption(waystone.visibility().ordinal());
        builder.dropdown(dropdownBuilder);

        builder.validResultHandler(customFormResponse -> {
            int visibility = customFormResponse.asDropdown();
            storage.updateWaystone(new StoredWaystone(waystone.id(), waystone.name(), waystone.owner(), Visibility.valueByNumber(visibility), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }


    /**
     * Opens the Waystone rename menu for the player
     * @param player The player that wants to rename the waystone
     * @param lcr Whether the screen was called the first time by placing the waystone or because the name was already taken or because the chosen Category was private/invalid
     */
    public void setCategory(Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {

        if (!waystone.checkTeleport(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

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
        for (Category c : storage.getCategories()) {
            dropdownBuilder2.option(c.name());
            list.add(c);
        }
        int defaultOption = list.indexOf(waystone.category());
        if(defaultOption == -1) defaultOption = 0;
        dropdownBuilder2.defaultOption(defaultOption);
        builder.dropdown(dropdownBuilder2);

        builder.validResultHandler(customFormResponse -> {
            int category = customFormResponse.asDropdown() + 1;
            Category storedCategory = storage.getCategory(category);
            if (storedCategory == null) {
                settingsFull(player, waystone, LastCreationResult.CATEGORY_INVALID);
                return;
            }
            if (!storedCategory.isPublic() && !storage.forceMode(player) && !player.hasPermission("waystones.category")) {
                settingsFull(player, waystone, LastCreationResult.CATEGORY_PRIVATE);
                return;
            }

            storage.updateWaystone(new StoredWaystone(waystone.id(), waystone.name(), waystone.owner(), waystone.visibility(), storedCategory, waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }



    /**
     * Opens the accesslist edit menu for the player
     * @param player Player that wants to edit the waystone
     * @param waystone Waystone to edit
     */
    public void accessSettings(@NotNull Player player, @NotNull StoredWaystone waystone) {

        SimpleForm.Builder builder = SimpleForm.builder().title("Waystone " + waystone.id()).content("Waystone bearbeiten");

        builder.button("Zugriffsliste ansehen");
        builder.button("Spieler hinzufügen");
        builder.button("Spieler entfernen");

        builder.validResultHandler(simpleFormResponse -> {
            switch (simpleFormResponse.clickedButtonId()) {
                case 0 -> accessView(player, waystone);
                case 1 -> accessAdd(player, waystone, LastCreationResult.FIRST_CALL);
                case 2 -> accessRemove(player, waystone);
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    /**
     * Opens the accesslist of a waystone
     * @param player Player that wants to see the list
     * @param waystone Waystone the list is requested from
     */
    public void accessView(@NotNull Player player, @NotNull StoredWaystone waystone) {

        SimpleForm.Builder builder = SimpleForm.builder().title("Waystone " + waystone.id()).content("Zugriffsliste");

        for (OfflinePlayer p : storage.accesslist(waystone.id())) {
            builder.button(p.getName());
        }

        builder.validResultHandler(simpleFormResponse -> {});

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    /**
     * Opens the menu to add a player to the accesslist
     * @param player Player that wants to edit the list
     * @param waystone Waystone the list should be changed of
     */
    public void accessAdd(@NotNull Player player, @NotNull StoredWaystone waystone, LastCreationResult lcr) {

        if (!waystone.checkTeleport(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

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
            if (storage.onAccesslist(p, waystone.id())) {
                accessAdd(player, waystone, LastCreationResult.PLAYER_EXISTING);
                return;
            }
            storage.addAccess(p, waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

    /**
     * Opens the menu to remove a player from the accesslist
     * @param player Player that wants to edit the list
     * @param waystone Waystone the list should be changed of
     */
    public void accessRemove(@NotNull Player player, @NotNull StoredWaystone waystone) {

        if (!waystone.checkPermission(player)) {
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(TextColor.color(255, 0, 0)));
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title("Waystone " + waystone.id());

        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        dropdownBuilder.text("Spieler zum Entfernen");

        List<OfflinePlayer> list = new ArrayList<>();
        for (OfflinePlayer p : storage.accesslist(waystone.id())) {
            dropdownBuilder.option(p.getName());
            list.add(p);
        }
        builder.dropdown(dropdownBuilder);

        builder.validResultHandler(customFormResponse -> {
            OfflinePlayer p = list.get(customFormResponse.asDropdown());
            storage.removeAccess(p, waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
