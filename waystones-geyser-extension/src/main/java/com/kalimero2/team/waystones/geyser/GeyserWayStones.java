package com.kalimero2.team.waystones.geyser;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.entity.EntityDefinition;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.item.custom.CustomItemData;
import org.geysermc.geyser.api.item.custom.CustomItemOptions;

public class GeyserWayStones implements Extension {

    public static EntityDefinition WAYSTONE_ENTITY;
/*
    @Subscribe
    public void onGeyserDefineEntities(GeyserDefineEntitiesEvent event) {
        WAYSTONE_ENTITY = EntityDefinition.builder()
                .identifier(EntityIdentifier.builder()
                        .identifier("klm2team:waystone")
                        .summonable(false)
                        .spawnEgg(false)
                        .build())
                .width(1.0f)
                .height(1.0f)
                .offset(1.0f)
                .build();
        event.definitions().add(WAYSTONE_ENTITY);
    }

    @Subscribe
    public void onServerSpawnEntity(ServerSpawnEntityEvent event) {
        if (event.entityDefinition().entityIdentifier().identifier().equals("minecraft:armor_stand")) {
            event.setEntityDefinition(WAYSTONE_ENTITY);
        }
    }*/

    @Subscribe
    public void onGeyserDefineCustomItems(GeyserDefineCustomItemsEvent event) {
        CustomItemOptions itemOptions = CustomItemOptions.builder()
                .customModelData(22022)
                .build();
        CustomItemData data = CustomItemData.builder()
                .name("waystone")
                .customItemOptions(itemOptions)
                .build();

        event.register("minecraft:stone_brick_wall", data);
    }

}
