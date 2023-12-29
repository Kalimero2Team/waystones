package com.kalimero2.team.waystones.paper.ui.screen;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class FloodgateButtonScreen implements GenericScreen {

    private final HashMap<ButtonComponent, Consumer<Player>> buttons = new HashMap<>();
    private final ButtonScreen buttonScreen;

    protected FloodgateButtonScreen(ButtonScreen buttonScreen) {
        this.buttonScreen = buttonScreen;
    }

    @NotNull
    private static String compomentToString(Component title1) {
        return PlainTextComponentSerializer.plainText().serialize(title1);
    }

    @Override
    // TODO: Change to new Cumulus API?
    @SuppressWarnings("deprecation")
    public void open(Player player) {
        SimpleForm.Builder builder = SimpleForm.builder();
        String title = compomentToString(buttonScreen.getTitle());
        builder.title(title);
        builder.content(buttonScreen.getLabel());

        buttonScreen.getButtons().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(ButtonScreen.Button::slot)))
                .forEach(entry -> {
                    ButtonScreen.Button button = entry.getKey();
                    Consumer<Player> consumer = entry.getValue();
                    ButtonComponent buttonComponent = ButtonComponent.of(compomentToString(button.name()));
                    buttons.put(buttonComponent, consumer);
                    builder.button(buttonComponent);
                });


        builder.validResultHandler((simpleForm, simpleFormResponse) -> {
            Consumer<Player> consumer = buttons.get(simpleFormResponse.getClickedButton());
            if (consumer != null) {
                consumer.accept(player);
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }
}
