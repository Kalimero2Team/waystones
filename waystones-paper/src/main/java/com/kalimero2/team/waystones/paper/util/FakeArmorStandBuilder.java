package com.kalimero2.team.waystones.paper.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class FakeArmorStandBuilder {
    private Component name;
    private World world;
    private double x;
    private double y;
    private double z;
    private float yaw = 0;
    private float pitch = 0;
    private boolean small = false;
    private boolean visible = true;
    private boolean glowing = false;
    private boolean showArms = false;
    private boolean showBasePlate = true;
    private boolean showName = false;
    private boolean hasVisualFire = false;
    private ItemStack headItem = null;
    private ItemStack chestItem = null;
    private ItemStack legsItem = null;
    private ItemStack feetItem = null;
    private ItemStack mainHandItem = null;
    private ItemStack offHandItem = null;

    public FakeArmorStandBuilder setName(Component name) {
        this.name = name;
        return this;
    }

    public FakeArmorStandBuilder setWorld(World world) {
        this.world = world;
        return this;
    }

    public FakeArmorStandBuilder setX(double x) {
        this.x = x;
        return this;
    }

    public FakeArmorStandBuilder setY(double y) {
        this.y = y;
        return this;
    }

    public FakeArmorStandBuilder setZ(double z) {
        this.z = z;
        return this;
    }

    public FakeArmorStandBuilder setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public FakeArmorStandBuilder setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public FakeArmorStandBuilder setSmall(boolean small) {
        this.small = small;
        return this;
    }

    public FakeArmorStandBuilder setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public FakeArmorStandBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public FakeArmorStandBuilder setShowArms(boolean showArms) {
        this.showArms = showArms;
        return this;
    }

    public FakeArmorStandBuilder setShowBasePlate(boolean showBasePlate) {
        this.showBasePlate = showBasePlate;
        return this;
    }

    public FakeArmorStandBuilder setShowName(boolean showName) {
        this.showName = showName;
        return this;
    }

    public FakeArmorStandBuilder setHasVisualFire(boolean hasVisualFire) {
        this.hasVisualFire = hasVisualFire;
        return this;
    }

    public FakeArmorStandBuilder setHeadItem(ItemStack headItem) {
        this.headItem = headItem;
        return this;
    }

    public FakeArmorStandBuilder setChestItem(ItemStack chestItem) {
        this.chestItem = chestItem;
        return this;
    }

    public FakeArmorStandBuilder setLegsItem(ItemStack legsItem) {
        this.legsItem = legsItem;
        return this;
    }

    public FakeArmorStandBuilder setFeetItem(ItemStack feetItem) {
        this.feetItem = feetItem;
        return this;
    }

    public FakeArmorStandBuilder setMainHandItem(ItemStack mainHandItem) {
        this.mainHandItem = mainHandItem;
        return this;
    }

    public FakeArmorStandBuilder setOffHandItem(ItemStack offHandItem) {
        this.offHandItem = offHandItem;
        return this;
    }

    public FakeArmorStandBuilder setLocation(Location location) {
        setWorld(location.getWorld());
        setX(location.getX());
        setY(location.getY());
        setZ(location.getZ());
        return this;
    }

    public FakeArmorStand createFakeArmorStand() {
        return new FakeArmorStand(name, world, x, y, z, yaw, pitch, small, visible, glowing, showArms, showBasePlate, showName, hasVisualFire, headItem, chestItem, legsItem, feetItem, mainHandItem, offHandItem);
    }

    public FakeArmorStandBuilder copy() {
        FakeArmorStandBuilder builder = new FakeArmorStandBuilder();
        builder.name = name;
        builder.world = world;
        builder.x = x;
        builder.y = y;
        builder.z = z;
        builder.yaw = yaw;
        builder.pitch = pitch;
        builder.small = small;
        builder.visible = visible;
        builder.glowing = glowing;
        builder.showArms = showArms;
        builder.showBasePlate = showBasePlate;
        builder.showName = showName;
        builder.hasVisualFire = hasVisualFire;
        builder.headItem = headItem;
        builder.chestItem = chestItem;
        builder.legsItem = legsItem;
        builder.feetItem = feetItem;
        builder.mainHandItem = mainHandItem;
        builder.offHandItem = offHandItem;
        return builder;
    }
}