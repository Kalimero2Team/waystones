package com.kalimero2.team.waystones.paper;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SerializableWayStones implements ConfigurationSerializable {

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
