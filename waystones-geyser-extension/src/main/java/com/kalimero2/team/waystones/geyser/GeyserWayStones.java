package com.kalimero2.team.waystones.geyser;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.block.custom.CustomBlockData;
import org.geysermc.geyser.api.block.custom.component.BoxComponent;
import org.geysermc.geyser.api.block.custom.component.CustomBlockComponents;
import org.geysermc.geyser.api.block.custom.component.MaterialInstance;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.item.custom.CustomItemData;
import org.geysermc.geyser.api.item.custom.CustomItemOptions;
import org.geysermc.geyser.level.block.GeyserGeometryComponent;

public class GeyserWayStones implements Extension {

    @Subscribe
    public void onGeyserDefineCustomBlocks(GeyserDefineCustomBlocksEvent event) {
        BoxComponent waystoneBox = new BoxComponent(0, 0, 0, 1, 2, 1);

        CustomBlockData waystoneData = CustomBlockData.builder()
                .name("waystone")
                .components(CustomBlockComponents.builder()
                        .collisionBox(waystoneBox)
                        .selectionBox(waystoneBox)
                        .geometry(new GeyserGeometryComponent.GeometryComponentBuilder()
                                .identifier("geometry.waystone")
                                .build())
                        .materialInstance("*", MaterialInstance.builder().texture("kalimero2team_waystone").renderMethod("alpha_test").build())
                        .build())
                .build();


        event.register(waystoneData);

        event.registerOverride("minecraft:petrified_oak_slab[type=double,waterlogged=false]", waystoneData.blockStateBuilder().build());
    }

    @Subscribe
    public void onGeyserDefineCustomItems(GeyserDefineCustomItemsEvent event) {
        CustomItemOptions itemOptions = CustomItemOptions.builder()
                .customModelData(2)
                .build();
        CustomItemData data = CustomItemData.builder()
                .name("static_waystone")
                .icon("static_waystone_icon")
                .displayName("Waystone")
                .textureSize(32)
                .customItemOptions(itemOptions)
                .build();

        event.register("minecraft:stone_brick_wall", data);

        CustomItemOptions portable = CustomItemOptions.builder()
                .customModelData(3)
                .build();
        CustomItemData portableData = CustomItemData.builder()
                .name("portable_waystone_something")
                .icon("portable_waystone_icon")
                .displayName("Portable Waystone")
                .textureSize(32)
                .customItemOptions(portable)
                .build();
        event.register("minecraft:clock", portableData);
    }

}
