package com.kalimero2.team.waystones.paper;

import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import com.kalimero2.waystones.paper.SerializableWayStone;
import com.kalimero2.waystones.paper.SerializableWayStones;
import org.bukkit.persistence.PersistentDataType;

public interface WayStoneDataTypes {

    PersistentDataType<byte[], SerializableWayStone> WAY_STONE = new ConfigurationSerializableDataType<>(SerializableWayStone.class);
    PersistentDataType<byte[], SerializableWayStones> WAY_STONES = new ConfigurationSerializableDataType<>(SerializableWayStones.class);

}
