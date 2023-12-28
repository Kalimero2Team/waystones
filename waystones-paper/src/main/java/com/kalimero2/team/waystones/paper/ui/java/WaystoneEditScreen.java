package com.kalimero2.team.waystones.paper.ui.java;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.SmallInventoryScreen;
import net.kyori.adventure.text.Component;

public class WaystoneEditScreen extends SmallInventoryScreen {
    public WaystoneEditScreen(PaperWayStones plugin, StoredWaystone waystone) {
        super(plugin, Component.text("Edit Waystone (" + waystone.getName() + ")"));
        createButton(0, Component.text("Rename Waystone"), player -> {
            player.sendMessage("TODO: Implement");
        },3);

        createButton(2, Component.text("Change Access"), player -> {
            player.sendMessage("TODO: Implement");
        },1);

        createButton(4, Component.text("Transfer Ownership"), player -> {
            player.sendMessage("TODO: Implement");
        });

        createButton(6, Component.text("Change Category"), player -> {
            player.sendMessage("TODO: Implement");
        });

        createButton(8, Component.text("Delete Waystone"), player -> {
            new DeleteConfirmScreen(plugin, waystone).show(player);
        },2);
    }



}
