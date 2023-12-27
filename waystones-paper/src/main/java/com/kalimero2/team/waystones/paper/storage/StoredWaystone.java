package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static com.kalimero2.team.waystones.paper.PaperWayStones.manager;

public final class StoredWaystone {
    private final int id;
    private String name;
    private UUID owner;
    private Visibility visibility;
    private Category category;
    private final int chunk_x;
    private final int chunk_z;
    private final int block_x;
    private final int block_y;
    private final int block_z;
    private final UUID world;
    private int uses;

    public StoredWaystone(int id, String name, UUID owner, Visibility visibility, Category category, int chunk_x, int chunk_z, int block_x, int block_y, int block_z, UUID world, int uses) {
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
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.visibility = visibility;
        this.category = category;
        this.chunk_x = chunk_x;
        this.chunk_z = chunk_z;
        this.block_x = block_x;
        this.block_y = block_y;
        this.block_z = block_z;
        this.world = world;
        this.uses = uses;
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
        }
        return true;
    }

    public boolean visibleTo(Player player) {
        if (visibility.equals(Visibility.PUBLIC)) return true;
        if (owner.equals(player.getUniqueId())) return true;
        WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();
        if (manager.forceMode(player)) return true;
        return manager.hasAccess(player, id);
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public UUID owner() {
        return owner;
    }

    public void owner(UUID owner) {
        this.owner = owner;
    }

    public Visibility visibility() {
        return visibility;
    }

    public void visibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Category category() {
        return category;
    }

    public void category(Category category) {
        this.category = category;
    }

    public int chunk_x() {
        return chunk_x;
    }

    public int chunk_z() {
        return chunk_z;
    }

    public int block_x() {
        return block_x;
    }

    public int block_y() {
        return block_y;
    }

    public int block_z() {
        return block_z;
    }

    public UUID world() {
        return world;
    }

    public int uses() {
        return uses;
    }

    public void uses(int uses) {
        this.uses = uses;
    }

    public int getUses() {
        return uses;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StoredWaystone) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owner, visibility, category, chunk_x, chunk_z, block_x, block_y, block_z, world, uses);
    }

    @Override
    public String toString() {
        return "StoredWaystone[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "owner=" + owner + ", " +
                "visibility=" + visibility + ", " +
                "category=" + category + ", " +
                "chunk_x=" + chunk_x + ", " +
                "chunk_z=" + chunk_z + ", " +
                "block_x=" + block_x + ", " +
                "block_y=" + block_y + ", " +
                "block_z=" + block_z + ", " +
                "world=" + world + ", " +
                "uses=" + uses + ']';
    }
}
