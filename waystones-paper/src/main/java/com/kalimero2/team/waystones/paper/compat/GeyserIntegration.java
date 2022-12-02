package com.kalimero2.team.waystones.paper.compat;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.item.custom.CustomItemData;
import org.geysermc.geyser.api.item.custom.CustomItemOptions;

public class GeyserIntegration implements Extension {

    @Subscribe
    public void onPostInitialize(GeyserPostInitializeEvent event) {
        System.out.println("Waystones Geyser extension loaded!");
    }

    @Subscribe
    public void onGeyserPreInitializeEvent(GeyserDefineCustomItemsEvent event){
        CustomItemOptions itemOptions = CustomItemOptions.builder()
                .customModelData(22022)
                .build();
        CustomItemData data = CustomItemData.builder()
                .name("waystone")
                .customItemOptions(itemOptions)
                .build();

        event.register("minecraft:stone_brick_wall",data);
    }

}
