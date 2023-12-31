package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class InputScreen implements GenericScreen {

    private final PaperWayStones plugin;
    private final JavaInputScreen javaScreen;
    private final FloodgateInputScreen floodgateScreen;
    private final Component title;
    private final String label;
    private final Input input;
    private InputScreen(PaperWayStones plugin, Component title, String label, Input input) {
        this.plugin = plugin;
        this.title = title;
        this.label = label;
        this.input = input;
        this.javaScreen = new JavaInputScreen(this);
        this.floodgateScreen = new FloodgateInputScreen(this);
    }

    public static InputScreen.Builder builder() {
        return new InputScreen.Builder();
    }

    protected PaperWayStones getPlugin() {
        return plugin;
    }

    protected Component getTitle() {
        return title;
    }

    protected String getLabel() {
        return label;
    }

    protected Input getInput() {
        return input;
    }

    @Override
    public void open(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreen.open(player);
        } else {
            javaScreen.open(player);
        }
    }

    public static class Builder {
        private PaperWayStones plugin;
        private Component title;
        private String content;
        private Input input;

        public Builder plugin(PaperWayStones plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder input(Input input) {
            this.input = input;
            return this;
        }

        public InputScreen build() {
            return new InputScreen(plugin, title, content, input);
        }
    }

    public record Input(Component title, String placeholder, ItemStack itemLeft, ItemStack itemResult, BiFunction<Player, String, InputValidation> onSubmitted) {
        public Input(Component title, String placeholder, BiFunction<Player, String, InputValidation> onSubmitted) {
            this(title, placeholder, null, null, onSubmitted);
        }
    }

    public record InputValidation(boolean valid, String message){
    }

}
