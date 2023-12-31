package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Objects;

public class JavaInputScreen implements GenericScreen, Listener {

    private final PaperWayStones plugin;
    private final InputScreen inputScreen;
    private final Component anvilUIPrefix = MiniMessage.miniMessage().deserialize("<white><tr:space.-60><font:klm2:waystones>c</font><tr:space.-172>");


    protected JavaInputScreen(InputScreen inputScreen) {
        this.plugin = inputScreen.getPlugin();
        this.inputScreen = inputScreen;
    }


    @Override
    public void open(Player player) {
        ItemStack left = Objects.requireNonNullElse(inputScreen.getInput().itemLeft(), JavaButtonScreen.getButton(Component.text("Cancel"), 4, Material.PAPER));
        ItemStack result = Objects.requireNonNullElse(inputScreen.getInput().itemResult(), JavaButtonScreen.getButton(Component.text("Confirm"), 5, Material.PAPER));

        Component title = anvilUIPrefix.append(inputScreen.getTitle());
        String jsonTitle = JSONComponentSerializer.json().serialize(title);

        new AnvilGUI.Builder().jsonTitle(jsonTitle).itemLeft(left).itemOutput(result).text(inputScreen.getInput().placeholder()).onClick((n, state) -> {
            if (n == 0) {
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            } else if (n == 1) {
                player.sendActionBar(Component.text("Wie hast du diesen Knopf gefunden?"));
                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            }

            InputScreen.InputValidation inputValidation = inputScreen.getInput().onSubmitted().apply(player, state.getText());

            if (!inputValidation.valid()) {
                return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(inputValidation.message()));
            }

            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        }).plugin(plugin).open(player);

    }

}
