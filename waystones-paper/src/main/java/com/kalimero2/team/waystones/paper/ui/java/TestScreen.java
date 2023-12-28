package com.kalimero2.team.waystones.paper.ui.java;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.ui.SmallInventoryScreen;
import net.kyori.adventure.text.Component;

public class TestScreen extends SmallInventoryScreen {
    public TestScreen(PaperWayStones plugin) {
        super(plugin, Component.text("Test Screen"));
        createButton(0, Component.text("Test Button"), player -> {
            player.sendMessage("Test Button Clicked");
        });
    }



}
