package com.kalimero2.waystones.api;

import com.kalimero2.waystones.api.waystone.BaseWaystone;
import com.kalimero2.waystones.api.waystone.PlayerWaystone;
import com.kalimero2.waystones.api.waystone.TeamWaystone;

import java.util.UUID;

public interface WayStonesApi {

    static WayStonesApi getApi() {
        return WayStonesApiHolder.getApi();
    }

    PlayerWaystone createPlayerWaystone(UUID owner, UUID world, int x, int y, int z, String name);

    TeamWaystone createTeamWaystone(UUID world, int x, int y, int z, String name);

    BaseWaystone getWaystone(int id);

    void removeWaystone(BaseWaystone wayStone);

    void removeWaystone(int id);

}
