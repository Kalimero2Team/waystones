package com.kalimero2.waystones.api;

import java.util.UUID;

public interface WayStonesApi {

    static WayStonesApi getApi(){
        return WayStonesApiHolder.getApi();
    }

    void createWayStone(UUID player, UUID world, int x, int y, int z, String name);

    void removeWayStone(UUID world, int x, int y, int z);

}
