package com.kalimero2.team.waystones.paper.storage;

import java.util.UUID;

public record Waystone(int id,
                       String name,
                       UUID owner,
                       int chunk_x,
                       int chunk_z,
                       int block_x,
                       int block_y,
                       int block_z,
                       UUID world) {
    public Waystone {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }
        if (world == null) {
            throw new IllegalArgumentException("world cannot be null");
        }
        if (block_x >> 4 != chunk_x) {
            throw new IllegalArgumentException("block_x is not in chunk_x");
        }
        if (block_z >> 4 != chunk_z) {
            throw new IllegalArgumentException("block_z is not in chunk_z");
        }
    }

    protected Waystone(int id, String name, String owner_uuid, int chunkX, int chunkZ, int x, int y, int z, String world_uuid) {
        this(id, name, UUID.fromString(owner_uuid), chunkX, chunkZ, x, y, z, UUID.fromString(world_uuid));
    }
}
