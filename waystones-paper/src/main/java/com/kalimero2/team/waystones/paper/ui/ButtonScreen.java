package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings({"unused","UnusedReturnValue"})
public class ButtonScreen implements GenericScreen {

    public static ButtonScreen.Builder builder() {
        return new ButtonScreen.Builder();
    }

    private final PaperWayStones plugin;
    private final JavaButtonScreen javaScreen;
    private final FloodgateButtonScreen floodgateScreen;
    private final Component title;
    private final String label;
    private final HashMap<Button, Consumer<Player>> buttons;


    private ButtonScreen(PaperWayStones plugin, Component title, String label, HashMap<Button, Consumer<Player>> buttons) {
        this.plugin = plugin;
        this.title = title;
        this.label = label;
        this.buttons = buttons;
        this.javaScreen = new JavaButtonScreen(this);
        this.floodgateScreen = new FloodgateButtonScreen(this);
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

    protected HashMap<Button, Consumer<Player>> getButtons() {
        return buttons;
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
        private final HashMap<ButtonScreen.Button, Consumer<Player>> buttons = new HashMap<>();

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

        public Builder button(Button button, Consumer<Player> action) {
            buttons.put(button, action);
            return this;
        }

        public ButtonScreen build() {
            return new ButtonScreen(plugin, title, content, buttons);
        }
    }

    public record Button(Component name, int slot, int modelData, Material material) {

        public Button(Component name, int slot) {
            this(name, slot, 0);
        }

        public Button(Component name, int slot, int modelData) {
            this(name, slot, modelData, Material.PAPER);
        }

    }

}
