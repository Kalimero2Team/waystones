package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.LastCreationResult;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;


public class FloodgateIntegration {
    private final PaperWayStones plugin;
    private final Storage storage;

    public FloodgateIntegration(PaperWayStones plugins) {
        this.plugin = plugins;
        this.storage = plugin.getStorage();
    }

    public void menu(Player player) {

        CustomForm.Builder builder = CustomForm.builder().title("Waystones").label("Wähle einen Waystone aus!");


        StoredWaystone[] waystones = plugin.getStorage().getWaystones(player);

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
        for (Category c : storage.getCategories()) {
            dropdownBuilder2.option(c.name());
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
            Category storedCategory = storage.getCategory(category);
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

}
