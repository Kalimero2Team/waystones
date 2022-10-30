package com.kalimero2.waystones.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import com.kalimero2.waystones.api.WayStonesApi;
import com.kalimero2.waystones.api.WayStonesApiHolder;
import com.kalimero2.waystones.paper.command.CommandManager;
import com.kalimero2.waystones.paper.listener.WayStonesListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PaperWayStones extends JavaPlugin implements WayStonesApi {
    public static PaperWayStones plugin;
    public boolean bedrockSupport = false;
    public static final NamespacedKey WAYSTONE_KEY = new NamespacedKey("waystones", "waystone");
    public static final NamespacedKey WAYSTONE_LIST_KEY = new NamespacedKey("waystones", "waystone_list");
    public static final NamespacedKey WAYSTONE_VILLAGER = new NamespacedKey("waystones", "waystone_villager");

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        WayStonesApiHolder.setApi(this);

        CustomBlockData.registerListener(this);

        ConfigurationSerialization.registerClass(SerializableWayStones.class);
        ConfigurationSerialization.registerClass(SerializableWayStone.class);

        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            bedrockSupport = true;
            getLogger().info("Bedrock support enabled");
        } catch (ClassNotFoundException e) {
            bedrockSupport = false;
            getLogger().info("Floodgate not found, disabling bedrock support");
        }


        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().warning("Failed to register commands");
        }
        getServer().getPluginManager().registerEvents(new WayStonesListener(), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void createWayStone(UUID player, UUID world, int x, int y, int z, String name) {
        createWayStone(getServer().getPlayer(player),new Location(getServer().getWorld(world), x, y, z), name);
    }

    public void createWayStone(Player player, Location location, String name) {
        Location centerLocation = location.clone().toCenterLocation();

        SerializableWayStone data = new SerializableWayStone(player.getUniqueId().toString(), name, centerLocation);

        centerLocation.getWorld().spawnParticle(Particle.REVERSE_PORTAL, centerLocation, 100, 0.0125, 0.0125, 0.0125,2);

        ArmorStand armorStand = centerLocation.getWorld().spawn(centerLocation.add(0,-0.5,0), ArmorStand.class);
        armorStand.setItem(EquipmentSlot.HEAD, getItem());
        armorStand.addDisabledSlots(EquipmentSlot.values());
        armorStand.setInvisible(true);
        armorStand.setCollidable(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setCustomNameVisible(true);
        armorStand.customName(Component.text(name));
        armorStand.setGravity(false);
        armorStand.getPersistentDataContainer().set(WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE, data);

        new BukkitRunnable(){
            @Override
            public void run() {

                Block bottom_block = centerLocation.getBlock();
                Block top_block = centerLocation.clone().add(0, 1, 0).getBlock();
                bottom_block.setType(Material.BARRIER);
                top_block.setType(Material.BARRIER);
                CustomBlockData bottomBlockData = new CustomBlockData(bottom_block, PaperWayStones.plugin);
                CustomBlockData topBlockData  = new CustomBlockData(top_block, PaperWayStones.plugin);
                bottomBlockData.set(WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE, data);
                topBlockData.set(WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE, data);

            }
        }.runTaskLater(PaperWayStones.plugin, 1);


        SerializableWayStones wayStones = getSerializableWayStones(location.getWorld());

        wayStones.addWayStone(wayStones.getNextId(), location);

        setSerializableWayStones(location.getWorld(), wayStones);
    }

    public void removeWayStone(UUID world, int x, int y, int z){
        removeWaystone(new Location(getServer().getWorld(world), x, y, z));
    }

    public void removeWaystone(Location location){
        Block block = location.getBlock();
        CustomBlockData customBlockData = new CustomBlockData(block, PaperWayStones.plugin);
        SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);

        location.getNearbyEntities(2,2,2).forEach(entity -> {
            if(entity instanceof ArmorStand armorStand && Objects.equals(entity.getPersistentDataContainer().get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE), wayStone)){
                armorStand.remove();
            }
        });

        Block second_block = location.clone().add(0, 1, 0).getBlock();
        if(new CustomBlockData(second_block,PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE)){
            second_block.setType(Material.AIR);
        }else{
            second_block = location.clone().add(0, -1, 0).getBlock();
            if(new CustomBlockData(second_block,PaperWayStones.plugin).has(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE)){
                second_block.setType(Material.AIR);
            }
        }


        SerializableWayStones wayStones = getSerializableWayStones(location.getWorld());

        int wayStone1 = wayStones.getWayStone(location);
        if(wayStone1 != -1){
            wayStones.removeWayStone(wayStone1);
        }

        setSerializableWayStones(location.getWorld(), wayStones);
    }

    public SerializableWayStones getSerializableWayStones(World world) {
        PersistentDataContainer persistentDataContainer = world.getPersistentDataContainer();
        SerializableWayStones wayStones = new SerializableWayStones(new HashMap<>(),0);

        if(persistentDataContainer.has(PaperWayStones.WAYSTONE_LIST_KEY)){
            wayStones = persistentDataContainer.getOrDefault(PaperWayStones.WAYSTONE_LIST_KEY, WayStoneDataTypes.WAY_STONES, wayStones);
        }
        return wayStones;
    }

    public void setSerializableWayStones(World world, SerializableWayStones wayStones){
        world.getPersistentDataContainer().set(PaperWayStones.WAYSTONE_LIST_KEY, WayStoneDataTypes.WAY_STONES, wayStones);
    }

    public ItemStack getItem(){
        ItemStack item = new ItemStack(Material.STONE_BRICK_WALL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("WayStone").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setCustomModelData(22022);
        item.setItemMeta(itemMeta);
        return item;
    }
}
