package com.kalimero2.team.waystones.paper.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class FakeArmorStand {
    private final ArmorStand armorStand;

    private final ServerLevel level;
    private final double x;
    private final double y;
    private final double z;
    private Component name = Component.empty();
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private boolean small = false;
    private boolean visible = true;
    private boolean glowing = false;
    private boolean showArms = false;
    private boolean showBasePlate = true;
    private boolean showName = false;
    private boolean hasVisualFire = false;
    private ItemStack headItem = ItemStack.EMPTY;
    private ItemStack chestItem = ItemStack.EMPTY;
    private ItemStack legsItem = ItemStack.EMPTY;
    private ItemStack feetItem = ItemStack.EMPTY;
    private ItemStack mainHandItem = ItemStack.EMPTY;
    private ItemStack offHandItem = ItemStack.EMPTY;


    public FakeArmorStand(Location location) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    public FakeArmorStand(World world, double x, double y, double z) {
        this(((CraftWorld) world).getHandle(), x, y, z);
    }

    private FakeArmorStand(ServerLevel level, double x, double y, double z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;

        this.armorStand = createArmorStand();
    }

    private static ItemStack fromBukkit(org.bukkit.inventory.ItemStack itemStack) {
        return itemStack == null ? ItemStack.EMPTY : ItemStack.fromBukkitCopy(itemStack);
    }

    public void showForPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(armorStand);
        connection.send(addEntityPacket);

        updateForPlayer(player);
    }

    public void updateForPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;

        armorStand.getBukkitEntity().customName(name);
        armorStand.setYRot(yaw);
        armorStand.setXRot(pitch);
        armorStand.setSmall(small);
        armorStand.setInvisible(!visible);
        armorStand.setGlowingTag(glowing);
        armorStand.setShowArms(showArms);
        armorStand.setNoBasePlate(!showBasePlate);
        armorStand.setCustomNameVisible(showName);
        armorStand.getBukkitEntity().setVisualFire(hasVisualFire);

        List<SynchedEntityData.DataValue<?>> defaultValues = armorStand.getEntityData().getNonDefaultValues();
        if (defaultValues != null) {
            ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(armorStand.getId(), defaultValues);
            connection.send(dataPacket);
        }

        List<Pair<EquipmentSlot, ItemStack>> equipmentList = Lists.newArrayListWithCapacity(6);

        if (headItem != null) equipmentList.add(new Pair<>(EquipmentSlot.HEAD, headItem));
        if (chestItem != null) equipmentList.add(new Pair<>(EquipmentSlot.CHEST, chestItem));
        if (legsItem != null) equipmentList.add(new Pair<>(EquipmentSlot.LEGS, legsItem));
        if (feetItem != null) equipmentList.add(new Pair<>(EquipmentSlot.FEET, feetItem));
        if (mainHandItem != null) equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, mainHandItem));
        if (offHandItem != null) equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, offHandItem));

        ClientboundSetEquipmentPacket setEquipmentPacket = new ClientboundSetEquipmentPacket(armorStand.getId(), equipmentList);
        connection.send(setEquipmentPacket);
    }

    public void hideForPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(armorStand.getId());
        connection.send(packet);
    }


    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public boolean isShowArms() {
        return showArms;
    }

    public void setShowArms(boolean showArms) {
        this.showArms = showArms;
    }

    public boolean isShowBasePlate() {
        return showBasePlate;
    }

    public void setShowBasePlate(boolean showBasePlate) {
        this.showBasePlate = showBasePlate;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public boolean isHasVisualFire() {
        return hasVisualFire;
    }

    public void setHasVisualFire(boolean hasVisualFire) {
        this.hasVisualFire = hasVisualFire;
    }

    public org.bukkit.inventory.ItemStack getHeadItem() {
        return headItem.asBukkitCopy();
    }

    public void setHeadItem(org.bukkit.inventory.ItemStack headItem) {
        this.headItem = fromBukkit(headItem);
    }

    public org.bukkit.inventory.ItemStack getChestItem() {
        return chestItem.asBukkitCopy();
    }

    public void setChestItem(org.bukkit.inventory.ItemStack chestItem) {
        this.chestItem = fromBukkit(chestItem);
    }

    public org.bukkit.inventory.ItemStack getLegsItem() {
        return legsItem.asBukkitCopy();
    }

    public void setLegsItem(org.bukkit.inventory.ItemStack legsItem) {
        this.legsItem = fromBukkit(legsItem);
    }

    public org.bukkit.inventory.ItemStack getFeetItem() {
        return feetItem.asBukkitCopy();
    }

    public void setFeetItem(org.bukkit.inventory.ItemStack feetItem) {
        this.feetItem = fromBukkit(feetItem);
    }

    public org.bukkit.inventory.ItemStack getMainHandItem() {
        return mainHandItem.asBukkitCopy();
    }

    public void setMainHandItem(org.bukkit.inventory.ItemStack mainHandItem) {
        this.mainHandItem = fromBukkit(mainHandItem);
    }

    public org.bukkit.inventory.ItemStack getOffHandItem() {
        return offHandItem.asBukkitCopy();
    }

    public void setOffHandItem(org.bukkit.inventory.ItemStack offHandItem) {
        this.offHandItem = fromBukkit(offHandItem);
    }

    @NotNull
    private ArmorStand createArmorStand() {
        ArmorStand armorStand = new ArmorStand(level, x, y, z);
        return armorStand;
    }
}
