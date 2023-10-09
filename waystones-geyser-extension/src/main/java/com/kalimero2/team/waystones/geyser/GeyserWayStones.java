package com.kalimero2.team.waystones.geyser;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
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

        CustomBlockData portableWaystoneData = CustomBlockData.builder()
                .name("portable_waystone")
                .components(CustomBlockComponents.builder()
                        .collisionBox(waystoneBox)
                        .selectionBox(waystoneBox)
                        .geometry(new GeyserGeometryComponent.GeometryComponentBuilder()
                                .identifier("geometry.portable_waystone")
                                .build())
                        .materialInstance("*", MaterialInstance.builder().texture("kalimero2team_portable_waystone").renderMethod("alpha_test").build())
                        .build())
                .build();

        event.register(waystoneData);
        event.register(portableWaystoneData);

        event.registerOverride("minecraft:petrified_oak_slab[type=double,waterlogged=false]", waystoneData.blockStateBuilder().build());
        event.registerOverride("minecraft:petrified_oak_slab[type=top,waterlogged=false]", portableWaystoneData.blockStateBuilder().build());

        System.out.println("Registered custom waystone block");
    }

    @Subscribe
    public void onGeyserDefineCustomItems(GeyserDefineCustomItemsEvent event) {
        CustomItemOptions itemOptions = CustomItemOptions.builder()
                .customModelData(2)
                .build();
        CustomItemData data = CustomItemData.builder()
                .name("waystone")
                .displayName("Waystone")
                .customItemOptions(itemOptions)
                .build();

        event.register("minecraft:stone_brick_wall", data);


        CustomItemOptions portable = CustomItemOptions.builder()
                .customModelData(3)
                .build();
        CustomItemData portableData = CustomItemData.builder()
                .name("portable_waystone")
                .displayName("Portable Waystone")
                .customItemOptions(portable)
                .build();

        event.register("minecraft:clock", portableData);

        System.out.println("Registered custom waystone item");
    }

}
