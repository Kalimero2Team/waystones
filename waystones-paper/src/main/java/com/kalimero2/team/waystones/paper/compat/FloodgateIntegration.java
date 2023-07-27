package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Storage;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.util.SortMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

import java.util.List;
import java.util.Locale;


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

        /*
        DropdownComponent.Builder dropdownBuilder = DropdownComponent.builder();
        for (StoredWaystone waystone : waystones) {
            //builder.component((Component) ButtonComponent.of(waystone.name()));
            dropdownBuilder.option(waystone.name());
        }
        builder.dropdown(dropdownBuilder);

        builder.validResultHandler(customFormResponse -> {
            int clickedButtonId = customFormResponse.asDropdown();
            System.out.println(clickedButtonId);
            StoredWaystone waystone = waystones[clickedButtonId];
            player.chat("/waystone tp " + waystone.id());
        });

         */

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
     * @param nameTaken Whether the screen was called the first time by placing the waystone or because the name was already taken
     */
    public void create(Player player, Location location, ItemStack stack, boolean nameTaken) {
        CustomForm.Builder builder = CustomForm.builder().title("Waystones").label("Wähle einen Waystone aus!");

        if (nameTaken) builder.label("Dieser Name ist bereits vergeben! Bitte wähle einen anderen Namen.");

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
            if (storage.nameFree(input)) {
                storage.addWaystone(input, player.getUniqueId(), 0, location.getChunk().getX(), location.getChunk().getZ(), location.blockX(), location.blockY(), location.blockZ(), location.getWorld().getUID());
                stack.setAmount(stack.getAmount() - 1);
                player.sendMessage(Component.text(""));
            }
            else {
                create(player, location, stack, true);
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }

}
