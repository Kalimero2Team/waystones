package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.kalimero2.team.waystones.paper.PaperWayStones.manager;

public record StoredWaystone(int id,
                             String name,
                             UUID owner,
                             Visibility visibility,
                             Category category,
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
        if (category == null) {
            category = Category.NONE;
        }
    }

    public StoredWaystone(int id, String name, String owner_uuid, int visibility, int category, int x, int y, int z, String world_uuid, int uses) {
        this(id, name, UUID.fromString(owner_uuid), Visibility.valueByNumber(visibility), manager.getCategory(category), x >> 4, z >> 4, x, y, z, UUID.fromString(world_uuid), uses);
    }

    public Location location() {
        return new Location(Bukkit.getWorld(world), block_x, block_y, block_z);
    }

    public boolean checkTeleport(Player player) {
        if (!visibility.equals(Visibility.PRIVATE)) return true;
        if (owner.equals(player.getUniqueId())) return true;
        WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();
        if (manager.forceMode(player)) return true;
        return manager.hasAccess(player, id);
    }

    public boolean checkPermission(CommandSender sender) {
        if (sender instanceof Player player) {
            if (owner.equals(player.getUniqueId())) return true;
            WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();
            return manager.forceMode(player);
        } return true;
    }

    public boolean visibleTo(Player player) {
        if (visibility.equals(Visibility.PUBLIC)) return true;
        if (owner.equals(player.getUniqueId())) return true;
        WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();
        if (manager.forceMode(player)) return true;
        return manager.hasAccess(player, id);
    }


}
