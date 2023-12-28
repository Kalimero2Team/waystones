package com.kalimero2.team.waystones.paper.ui.java;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.SmallInventoryScreen;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;

public class DeleteConfirmScreen extends SmallInventoryScreen {
    public DeleteConfirmScreen(PaperWayStones plugin, StoredWaystone waystone) {
        super(plugin, Component.text("Delete Waystone " + waystone.getName() + "?"));

        createButton(12, Component.text("Yes"), player -> {

        },5);

        createButton(14, Component.text("No"), HumanEntity::closeInventory,4);
    }



}
