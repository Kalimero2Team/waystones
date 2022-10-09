package com.kalimero2.waystones.paper;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SerializableWayStone implements ConfigurationSerializable {

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
