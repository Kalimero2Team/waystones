package com.kalimero2.waystones.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
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
            if(wayStone != null){
                hashMap.put(integer+" - "+wayStone.getName(), integer);
                builder.button(integer + " - " + wayStone.getName());
            }
        }

        builder.responseHandler((form, responseData) -> {
            SimpleFormResponse response = form.parseResponse(responseData);

            ButtonComponent button = response.getClickedButton();
            if(button != null && hashMap.containsKey(button.getText())){
                player.chat("/waystone tp "+hashMap.get(button.getText()));
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
