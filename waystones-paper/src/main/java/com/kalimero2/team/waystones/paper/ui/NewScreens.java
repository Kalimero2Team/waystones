package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.ui.screen.ButtonScreen;
import com.kalimero2.team.waystones.paper.ui.screen.InputScreen;
import com.kalimero2.team.waystones.paper.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class NewScreens {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    public NewScreens(PaperWayStones plugin){
        this.plugin = plugin;
        this.manager = this.plugin.getManager();
    }


    /**
     * Opens the edit menu for the player
     * @param player Player that wants to edit the waystone
     * @param waystone Waystone to edit
     */
    public void settings(@NotNull Player player, @NotNull StoredWaystone waystone) {
        ButtonScreen build = createSettingsScreen(waystone);
        build.open(player);
    }


    /**
     * Opens the Waystone rename menu for the player
     * @param player The player that wants to rename the waystone
     */
    public void rename(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!waystone.checkTeleport(player)) {
            // TODO: Change Message to-far-away or something
            player.sendMessage(Component.translatable("waystones.nopermission.edit").fallback("Du hast keine Berechtigung diesen Waystone zu bearbeiten!").asComponent().color(ColorUtil.RED));
            return;
        }

        InputScreen build = createRenameScreen(waystone);
        build.open(player);
    }

    private InputScreen createRenameScreen(StoredWaystone waystone) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystone " + waystone.name())).plugin(plugin);
        builder.content("Nenne den Waystone um");
        builder.input(new InputScreen.Input(Component.text("Waystone Name"), waystone.name(), (player, input) -> {

            if (input == null || input.isEmpty()) {
                return new InputScreen.InputValidation(false, "Der Name darf nicht leer sein!");
            }

            if (input.length() > 16) {
                return new InputScreen.InputValidation(false, "Der Name darf nicht länger als 16 Zeichen sein!");
            }

            if (manager.isNameUsed(input)) {
                return new InputScreen.InputValidation(false, "Dieser Name wird bereits verwendet!");
            }

            manager.updateWaystone(new StoredWaystone(waystone.id(), input, waystone.owner(), waystone.visibility(), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses()));
            return new InputScreen.InputValidation(true, null);
        }));

        return builder.build();
    }


    private ButtonScreen createSettingsScreen(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).content("Waystone bearbeiten");
        builder.plugin(plugin);

        builder.button(new ButtonScreen.Button(Component.text("Umbenennen"),0,3), player -> {
            rename(player, waystone);
        });
        builder.button(new ButtonScreen.Button(Component.text("Zugriff verwalten"), 2,1), player -> {

        });
        builder.button(new ButtonScreen.Button(Component.text("Eigentümer ändern"),4,0), player -> {

        });
        builder.button(new ButtonScreen.Button(Component.text("Kategorie ändern"),6,0), player -> {

        });
        builder.button(new ButtonScreen.Button(Component.text("Löschen"),8,2), player -> {

        });

        return builder.build();
    }


}
