package com.kalimero2.waystones.api.waystone;

import java.util.UUID;

public class PlayerWaystone extends BaseWaystone {

    private final UUID owner;

    public PlayerWaystone(int id, int block_x, int block_y, int block_z, UUID world, UUID owner) {
        super(id, block_x, block_y, block_z, world);
        this.owner = owner;
    }

    public UUID owner() {
        return owner;
    }
}
