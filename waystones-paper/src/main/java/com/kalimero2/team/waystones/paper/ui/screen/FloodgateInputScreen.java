package com.kalimero2.team.waystones.paper.ui.screen;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

public class FloodgateInputScreen implements GenericScreen {

    private final InputScreen inputScreen;

    protected FloodgateInputScreen(InputScreen inputScreen) {
        this.inputScreen = inputScreen;
    }

    @NotNull
    private static String compomentToString(Component title) {
        return PlainTextComponentSerializer.plainText().serialize(title);
    }

    @Override
    public void open(Player player) {
        open(player, null);
    }

    private void open(Player player, InputScreen.InputValidation lastValidation) {
        CustomForm.Builder builder = CustomForm.builder().title(compomentToString(inputScreen.getTitle()));

        builder.input(inputScreen.getLabel(), inputScreen.getInput().placeholder(), inputScreen.getInput().placeholder());
        if (lastValidation != null) {
            builder.label(lastValidation.message());
        }

        builder.validResultHandler(customFormResponse -> {
            String input = customFormResponse.asInput(0);
            InputScreen.InputValidation inputValidation = inputScreen.getInput().onSubmitted().apply(player, input);

            if (inputValidation.valid()) {
                return;
            }
            open(player, inputValidation);
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }
}
