package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.ui.screen.ButtonScreen;
import com.kalimero2.team.waystones.paper.ui.screen.InputScreen;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class NewScreens {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    public NewScreens(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager();
    }

    /**
     * Opens the setup menu for the player
     *
     * @param player   Player that wants to set up the waystone
     * @param location Location of the waystone
     * @param stack    ItemStack that was used to create the waystone
     */
    public void setup(@NotNull Player player, @NotNull Location location, @NotNull ItemStack stack) {
        InputScreen screen = createSetupScreen(location, stack);
        screen.open(player);
    }


    /**
     * Opens the owner change menu for the player
     *
     * @param player   Player (old Owner that wants to change the owner
     * @param waystone Waystone to change the owner
     */
    public void changeOwner(Player player, StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        InputScreen screen = createChangeOwnerScreen(waystone);
        screen.open(player);
    }


    /**
     * Opens the edit menu for the player
     *
     * @param player   Player that wants to edit the waystone
     * @param waystone Waystone to edit
     */
    public void settings(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        ButtonScreen build = createSettingsScreen(waystone);
        build.open(player);
    }


    /**
     * Opens the Waystone rename menu for the player
     *
     * @param player   The player that wants to rename the waystone
     * @param waystone The waystone to rename
     */
    public void rename(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        InputScreen build = createRenameScreen(waystone);
        build.open(player);
    }


    /**
     * Opens a confirm screen for the player to delete the waystone
     *
     * @param player   Player that wants to delete the waystone
     * @param waystone Waystone to delete
     */
    public void delete(Player player, StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        ButtonScreen build = createDeleteScreen(waystone);
        build.open(player);
    }


    /**
     * Opens the access settings menu for the player
     *
     * @param player   Player that wants to change the access settings
     * @param waystone Waystone to change the access settings
     */
    public void accessSettings(Player player, StoredWaystone waystone) {
        if (!waystone.checkPermission(player)) return;
        ButtonScreen build = createAccessSettings(waystone);
        build.open(player);
    }


    private InputScreen createSetupScreen(@NotNull Location location, ItemStack stack) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystones")).plugin(plugin);
        builder.content("Name des Waystones");
        builder.input(new InputScreen.Input(Component.text("Name des Waystones"), "", (player, input) -> {

            InputScreen.InputValidation nameValidation = validateWaystoneName(input);
            if (nameValidation != null) return nameValidation;

            StoredWaystone waystone = plugin.getManager().createWaystone(input, player.getUniqueId(), Visibility.PUBLIC.id(), Category.NONE.id(), location);
            plugin.getDisplayManager().updateDisplay(waystone);

            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                stack.setAmount(stack.getAmount() - 1);
            }

            return new InputScreen.InputValidation(true, null);
        }));

        return builder.build();
    }

    private ButtonScreen createDeleteScreen(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).content("Waystone löschen");
        builder.plugin(plugin);

        builder.button(new ButtonScreen.Button(Component.text("Löschen"), 5, 5), player -> {
            plugin.getDisplayManager().clearDisplay(waystone);
            if (manager.removeWaystone(waystone.id())) {
                player.sendMessage(Component.text("Waystone wurde entfernt!", TextUtil.GREEN));
                player.getInventory().addItem(plugin.getStatic());
            } else {
                player.sendMessage(Component.text("Waystone konnte nicht entfernt werden.", TextUtil.RED));
            }
            player.closeInventory();
        });
        builder.button(new ButtonScreen.Button(Component.text("Abbrechen"), 3, 4), HumanEntity::closeInventory);

        return builder.build();
    }

    private InputScreen createRenameScreen(@NotNull StoredWaystone waystone) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystone " + waystone.name())).plugin(plugin);
        builder.content("Nenne den Waystone um");
        builder.input(new InputScreen.Input(Component.text("Waystone Name"), waystone.name(), (player, input) -> {

            InputScreen.InputValidation nameValidation = validateWaystoneName(input);
            if (nameValidation != null) return nameValidation;

            boolean renamed = manager.renameWaystone(waystone.id(), input);

            if (!renamed) {
                // This shouldn't happen, because we already check the name above ...
                return new InputScreen.InputValidation(false, ":( Es ist ein Fehler aufgetreten!");
            }

            plugin.getDisplayManager().updateDisplay(manager.getWaystone(waystone.id()));

            return new InputScreen.InputValidation(true, null);
        }));

        return builder.build();
    }

    private ButtonScreen createSettingsScreen(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).content("Waystone bearbeiten");
        builder.plugin(plugin);

        builder.button(new ButtonScreen.Button(Component.text("Umbenennen"), 0, 3), player -> {
            plugin.getScreen().rename(player, waystone);
        });
        builder.button(new ButtonScreen.Button(Component.text("Zugriff verwalten"), 2, 1), player -> {
            plugin.getScreen().accessSettings(player, waystone);
        });
        builder.button(new ButtonScreen.Button(Component.text("Eigentümer ändern"), 4, 9), player -> {
            plugin.getScreen().transferOwnership(player, waystone);
        });
        builder.button(new ButtonScreen.Button(Component.text("Kategorie ändern"), 6, 8), player -> {
            plugin.getScreen().category(player, waystone);
        });
        builder.button(new ButtonScreen.Button(Component.text("Löschen"), 8, 2), player -> {
            delete(player, waystone);
        });

        return builder.build();
    }

    private ButtonScreen createAccessSettings(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).content("Zugriff Verwalten");
        builder.plugin(plugin);

        Visibility visibility = waystone.visibility();
        Visibility nextVisibility;
        String name;
        int modelData;

        if (visibility == Visibility.PRIVATE) {
            name = "Privat";
            nextVisibility = Visibility.PUBLIC;
            modelData = 10;
        } else if (visibility == Visibility.UNLISTED) {
            name = "Nicht gelistet";
            nextVisibility = Visibility.PRIVATE;
            modelData = 12;
        } else {
            name = "Öffentlich";
            nextVisibility = Visibility.UNLISTED;
            modelData = 11;
        }

        builder.button(new ButtonScreen.Button(Component.text("Sichtbarkeit: " + name), 0, modelData), player -> {
            manager.setVisibility(waystone.id(), nextVisibility);
            player.sendMessage(Component.translatable("waystones.visibility.set", TextColor.color(255, 73, 0), Component.text(waystone.name()), nextVisibility.text()));
            accessSettings(player, manager.getWaystone(waystone.id()));
        });

        if (!visibility.equals(Visibility.PUBLIC)) {
            builder.button(new ButtonScreen.Button(Component.text("Spieler hinzufügen"), 2, 6), player -> {
                plugin.getScreen().addAccess(player, waystone);
            });
            builder.button(new ButtonScreen.Button(Component.text("Spieler entfernen"), 4, 7), player -> {
                plugin.getScreen().removeAccess(player, waystone);
            });
        }


        return builder.build();
    }

    private InputScreen createChangeOwnerScreen(@NotNull StoredWaystone waystone) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystone " + waystone.name())).plugin(plugin);
        builder.content("Ändere den Eigentümer");
        builder.input(new InputScreen.Input(Component.text("Neuer Eigentümer"), "", (player, input) -> {
            if (input == null || input.isEmpty()) {
                return new InputScreen.InputValidation(false, "Der Name darf nicht leer sein!");
            }
            OfflinePlayer target = plugin.getServer().getOfflinePlayerIfCached(input);
            if (target == null) {
                return new InputScreen.InputValidation(false, "Dieser Spieler existiert nicht!");
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    StoredWaystone newWaystone = new StoredWaystone(waystone.id(), waystone.name(), target.getUniqueId(), waystone.visibility(), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses());
                    manager.updateWaystone(newWaystone);
                }
            }.runTask(plugin);


            return new InputScreen.InputValidation(true, null);
        }));


        return builder.build();
    }

    @Nullable
    private InputScreen.InputValidation validateWaystoneName(String input) {
        if (input == null || input.isEmpty()) {
            return new InputScreen.InputValidation(false, "Der Name darf nicht leer sein!");
        }

        if (input.length() > 16) {
            return new InputScreen.InputValidation(false, "Der Name darf nicht länger als 16 Zeichen sein!");
        }

        if (manager.isNameUsed(input)) {
            return new InputScreen.InputValidation(false, "Dieser Name wird bereits verwendet!");
        }
        return null;
    }

}
