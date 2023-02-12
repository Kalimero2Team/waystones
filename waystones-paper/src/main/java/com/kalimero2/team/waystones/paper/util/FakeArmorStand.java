package com.kalimero2.team.waystones.paper.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FakeArmorStand {
    private final Component name;
    private final ServerLevel level;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean small;
    private final boolean visible;
    private final boolean glowing;
    private final boolean showArms;
    private final boolean showBasePlate;
    private final boolean showName;
    private final boolean hasVisualFire;
    private final ItemStack headItem;
    private final ItemStack chestItem;
    private final ItemStack legsItem;
    private final ItemStack feetItem;
    private final ItemStack mainHandItem;
    private final ItemStack offHandItem;

    private final ArmorStand armorStand;

    FakeArmorStand(Component name, ServerLevel level, double x, double y, double z, float yaw, float pitch, boolean small, boolean visible, boolean glowing, boolean showArms, boolean showBasePlate, boolean showName, boolean hasVisualFire, net.minecraft.world.item.ItemStack headItem, net.minecraft.world.item.ItemStack chestItem, net.minecraft.world.item.ItemStack legsItem, net.minecraft.world.item.ItemStack feetItem, net.minecraft.world.item.ItemStack mainHandItem, net.minecraft.world.item.ItemStack offHandItem) {
        this.name = name;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.small = small;
        this.visible = visible;
        this.glowing = glowing;
        this.showArms = showArms;
        this.showBasePlate = showBasePlate;
        this.showName = showName;
        this.hasVisualFire = hasVisualFire;
        this.headItem = headItem;
        this.chestItem = chestItem;
        this.legsItem = legsItem;
        this.feetItem = feetItem;
        this.mainHandItem = mainHandItem;
        this.offHandItem = offHandItem;

        this.armorStand = createArmorStand();
    }

    FakeArmorStand(Component name, World world, double x, double y, double z, float yaw, float pitch, boolean small, boolean visible, boolean glowing, boolean showArms, boolean showBasePlate, boolean showName, boolean hasVisualFire, org.bukkit.inventory.ItemStack headItem, org.bukkit.inventory.ItemStack chestItem, org.bukkit.inventory.ItemStack legsItem, org.bukkit.inventory.ItemStack feetItem, org.bukkit.inventory.ItemStack mainHandItem, org.bukkit.inventory.ItemStack offHandItem) {
        this(name, ((CraftWorld) world).getHandle(), x, y, z, yaw, pitch, small, visible, glowing, showArms, showBasePlate, showName, hasVisualFire, fromBukkit(headItem), fromBukkit(chestItem), fromBukkit(legsItem), fromBukkit(feetItem), fromBukkit(mainHandItem), fromBukkit(offHandItem));
    }

    private static ItemStack fromBukkit(org.bukkit.inventory.ItemStack itemStack) {
        return itemStack == null ? null : ItemStack.fromBukkitCopy(itemStack);
    }

    public void sendToPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(armorStand);
        connection.send(addEntityPacket);
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


        // TODO: Add Option to Remove Armorstand when Player unloads Chunk
        // ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(armorStand.getId());
    }

    @NotNull
    private ArmorStand createArmorStand() {
        ArmorStand armorStand = new ArmorStand(level, x, y, z);
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
        return armorStand;
    }
}
