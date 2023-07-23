package com.kalimero2.team.waystones.paper.compat;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class LegacyConverter {

    private final PaperWayStones plugin;

    private final Logger LOGGER = Logger.getLogger("LegacyConverter");
    private final NamespacedKey WAYSTONE_KEY = new NamespacedKey("waystones", "waystone");
    private final NamespacedKey WAYSTONE_LIST_KEY = new NamespacedKey("waystones", "waystone_list");

    private final PersistentDataType<byte[], SerializableWayStone> WAY_STONE = new ConfigurationSerializableDataType<>(SerializableWayStone.class);
    private final PersistentDataType<byte[], SerializableWayStones> WAY_STONES = new ConfigurationSerializableDataType<>(SerializableWayStones.class);


    public LegacyConverter(PaperWayStones plugin) {
        this.plugin = plugin;
    }

    public void convert() {
        ConfigurationSerialization.registerClass(SerializableWayStones.class);
        ConfigurationSerialization.registerClass(SerializableWayStone.class);

        Server server = plugin.getServer();
        server.getWorlds().forEach(world -> {
            PersistentDataContainer persistentDataContainer = world.getPersistentDataContainer();
            if (persistentDataContainer.has(WAYSTONE_LIST_KEY)) {
                LOGGER.info("Converting Waystones in world " + world.getName());
                SerializableWayStones wayStones = persistentDataContainer.get(WAYSTONE_LIST_KEY, WAY_STONES);
                if (wayStones != null) {
                    wayStones.getWayStones().forEach((id, location) -> {
                        LOGGER.info("Converting Waystone with old id:" + id);
                        CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), plugin);
                        SerializableWayStone wayStone = customBlockData.get(WAYSTONE_KEY, WAY_STONE);
                        plugin.getStorage().addWaystone(wayStone.getName(), wayStone.getOwnerUUID(), 0, location.getChunk().getX(), location.getChunk().getZ(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), world.getUID());
                    });
                }
            } else {
                LOGGER.info("No Waystones found in world " + world.getName());
            }
        });

        plugin.getConfig().set("did-legacy-conversion", true);
        plugin.saveConfig();

        LOGGER.info("Conversion finished. Unregistering serialization classes ...");
        ConfigurationSerialization.unregisterClass(SerializableWayStones.class);
        ConfigurationSerialization.unregisterClass(SerializableWayStone.class);
    }

    @Deprecated(forRemoval = true)
    @SerializableAs("com.kalimero2.waystones.paper.SerializableWayStone")
    public static class SerializableWayStone implements ConfigurationSerializable {

        String owner_uuid;
        String name;
        Location location;

        public SerializableWayStone(String owner_uuid, String name, Location location) {
            this.owner_uuid = owner_uuid;
            this.name = name;
            this.location = location;
        }

        public static SerializableWayStone deserialize(Map<String, Object> map) {
            return new SerializableWayStone((String) map.get("owner"), (String) map.get("name"), (Location) map.get("location"));
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("owner", owner_uuid);
            map.put("name", name);
            map.put("location", location);
            return map;
        }

        public UUID getOwnerUUID() {
            return UUID.fromString(owner_uuid);
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SerializableWayStone that = (SerializableWayStone) o;
            return Objects.equals(owner_uuid, that.owner_uuid) && Objects.equals(name, that.name) && Objects.equals(location, that.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner_uuid, name, location);
        }

        @Override
        public String toString() {
            return "SerializableWayStone{" +
                    "owner_uuid='" + owner_uuid + '\'' +
                    ", name='" + name + '\'' +
                    ", location=" + location +
                    '}';
        }
    }

    @Deprecated(forRemoval = true)
    @SerializableAs("com.kalimero2.waystones.paper.SerializableWayStones")
    public static class SerializableWayStones implements ConfigurationSerializable {

        HashMap<Integer, Location> wayStones;
        Integer nextId;


        public SerializableWayStones(HashMap<Integer, Location> wayStones, Integer nextId) {
            this.wayStones = wayStones;
            this.nextId = nextId;
        }

        public static SerializableWayStones deserialize(Map<String, Object> map) {
            return new SerializableWayStones((HashMap<Integer, Location>) map.get("data"), (int) map.get("nextId"));
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("data", wayStones);
            map.put("nextId", nextId);
            return map;
        }

        public HashMap<Integer, Location> getWayStones() {
            return wayStones;
        }

        public void setWayStones(HashMap<Integer, Location> wayStones) {
            this.wayStones = wayStones;
        }

        public Location removeWayStone(int id) {
            return wayStones.remove(id);
        }

        public Location getWayStone(int id) {
            return wayStones.get(id);
        }

        public int getWayStone(Location location) {
            for (Map.Entry<Integer, Location> entry : wayStones.entrySet()) {
                if (entry.getValue().equals(location)) {
                    return entry.getKey();
                }
            }
            return -1;
        }
    }


}
