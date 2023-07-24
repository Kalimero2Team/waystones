package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public record StoredWaystone(int id,
                             String name,
                             UUID owner,
                             Visibility visibility,
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
        if (visibility == null) {
            visibility = Visibility.PUBLIC;
        }
    }

    public StoredWaystone(int id, String name, String owner_uuid, int visibility, int chunk_x, int chunk_z, int x, int y, int z, String world_uuid, int uses) {
        this(id, name, UUID.fromString(owner_uuid), Visibility.valueByNumber(visibility), chunk_x, chunk_z, x, y, z, UUID.fromString(world_uuid), uses);
    }

    public Location location() {
        return new Location(Bukkit.getWorld(world), block_x, block_y, block_z);
    }

    public boolean checkPlayer(Player player) {
        if (!visibility.equals(Visibility.PRIVATE)) return true;
        if (owner.equals(player.getUniqueId())) return true;
        return true; //TODO: Implement whitelist
        //Storage storage = PaperWayStones.getPlugin(PaperWayStones.class).getStorage();
        //if (storage.getWhitelist(id)).contains(player.getUniqueId);
    }
}
