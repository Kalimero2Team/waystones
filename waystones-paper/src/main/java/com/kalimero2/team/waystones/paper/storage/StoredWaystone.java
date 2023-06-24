package com.kalimero2.team.waystones.paper.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public record StoredWaystone(int id,
                             String name,
                             UUID owner,
                             boolean whitelisted,
                             int chunk_x,
                             int chunk_z,
                             int block_x,
                             int block_y,
                             int block_z,
                             UUID world,
                             int uses) {
    public StoredWaystone {
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

    protected StoredWaystone(int id, String name, String owner_uuid, boolean whitelisted, int x, int y, int z, String world_uuid, int uses) {
        this(id, name, UUID.fromString(owner_uuid), whitelisted, x >> 4, z >> 4, x, y, z, UUID.fromString(world_uuid), uses);
    }

    protected StoredWaystone(int id, String name, String owner_uuid, boolean whitelisted, int chunk_x, int chunk_z, int x, int y, int z, String world_uuid, int uses) {
        this(id, name, UUID.fromString(owner_uuid), whitelisted, chunk_x, chunk_z, x, y, z, UUID.fromString(world_uuid), uses);
    }

    public Location location() {
        return new Location(Bukkit.getWorld(world), block_x, block_y, block_z);
    }
}
