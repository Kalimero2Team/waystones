package com.kalimero2.waystones.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.Map;

public class BedrockCompat {
    public static void showBedrockForm(Player player){

        SimpleForm.Builder builder = SimpleForm.builder().title("WayStones").content("Wähle einen Waystone aus!");

        SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (Map.Entry<Integer, Location> entry : wayStones.getWayStones().entrySet()) {
            Integer integer = entry.getKey();
            Location location = entry.getValue();
            CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), PaperWayStones.plugin);
            SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
            hashMap.put(integer+" - "+wayStone.getName(), integer);
            builder.button(integer+" - "+wayStone.getName());
        }

        builder.responseHandler((simpleForm, response) -> {
            if(hashMap.containsKey(response)){
                player.chat("/waystone tp "+hashMap.get(response));
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
