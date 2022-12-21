package com.kalimero2.team.waystones.paper.compat;

import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LegacyConverter {

    public static void convert() {
        ConfigurationSerialization.registerClass(SerializableWayStones.class);
        ConfigurationSerialization.registerClass(SerializableWayStone.class);

        // TODO: Convert

        // ConfigurationSerialization.unregisterClass(SerializableWayStones.class);
        // ConfigurationSerialization.unregisterClass(SerializableWayStone.class);
    }


    @SerializableAs("com.kalimero2.waystones.paper.SerializableWayStone")
    public static class SerializableWayStone implements ConfigurationSerializable {

        String owner_uuid;
        String name;
        Location location;

        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("owner", owner_uuid);
            map.put("name", name);
            map.put("location", location);
            return map;
        }

        public static SerializableWayStone deserialize(Map<String,Object> map) {
            return new SerializableWayStone((String) map.get("owner"), (String) map.get("name"), (Location) map.get("location"));
        }

        public SerializableWayStone(String owner_uuid, String name, Location location) {
            this.owner_uuid = owner_uuid;
            this.name = name;
            this.location = location;
        }

        public String getOwnerUUID() {
            return owner_uuid;
        }

        public String getName() {
            return name;
        }

        public Location getLocation() {
            return location;
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

    @SerializableAs("com.kalimero2.waystones.paper.SerializableWayStones")
    public static class SerializableWayStones implements ConfigurationSerializable {

        HashMap<Integer, Location> wayStones;
        Integer nextId;


        @Override
        public @NotNull Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("data", wayStones);
            map.put("nextId", nextId);
            return map;
        }

        public static SerializableWayStones deserialize(Map<String,Object> map) {
            return new SerializableWayStones((HashMap<Integer, Location>) map.get("data"), (int) map.get("nextId"));
        }

        public SerializableWayStones(HashMap<Integer, Location> wayStones, Integer nextId) {
            this.wayStones = wayStones;
            this.nextId = nextId;
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

        public int getNextId() {
            if(nextId == null){
                nextId = 0;
            }
            nextId ++;
            return nextId;
        }

        public void addWayStone(int id, Location location) {
            wayStones.put(id, location);
        }

    }

    public static interface WayStoneDataTypes {

        PersistentDataType<byte[], SerializableWayStone> WAY_STONE = new ConfigurationSerializableDataType<>(SerializableWayStone.class);
        PersistentDataType<byte[], SerializableWayStones> WAY_STONES = new ConfigurationSerializableDataType<>(SerializableWayStones.class);

    }


}
