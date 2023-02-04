package com.kalimero2.waystones.api.waystone;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseWaystone {
    private final int id;
    private final int block_x;
    private final int block_y;
    private final int block_z;
    private final UUID world;

    protected BaseWaystone(int id, int block_x, int block_y, int block_z, UUID world) {
        this.id = id;
        this.block_x = block_x;
        this.block_y = block_y;
        this.block_z = block_z;
        this.world = world;
    }

    public int id() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BaseWaystone) obj;
        return this.id == that.id &&
                this.block_x == that.block_x &&
                this.block_y == that.block_y &&
                this.block_z == that.block_z &&
                Objects.equals(this.world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, block_x, block_y, block_z, world);
    }

    @Override
    public String toString() {
        return "BaseWaystone[" +
                "id=" + id + ", " +
                "block_x=" + block_x + ", " +
                "block_y=" + block_y + ", " +
                "block_z=" + block_z + ", " +
                "world=" + world + ']';
    }


}
