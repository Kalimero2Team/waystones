package com.kalimero2.team.waystones.paper.compat;

import com.jeff_media.customblockdata.CustomBlockData;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.SerializableWayStone;
import com.kalimero2.team.waystones.paper.SerializableWayStones;
import com.kalimero2.team.waystones.paper.WayStoneDataTypes;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.Map;

public class FloodgateIntegration {
    public static void showBedrockForm(Player player){

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        NamespacedKey favoriteSelectionModeKey = new NamespacedKey("waystones", "favorite_selection_mode");

        boolean favoriteSelectionMode;

        if (dataContainer.has(favoriteSelectionModeKey)) {
            if (dataContainer.get(favoriteSelectionModeKey, PersistentDataType.INTEGER) == 1) favoriteSelectionMode = true;
            else {
                favoriteSelectionMode = false;
            }
        } else {
            favoriteSelectionMode = false;
        }
        ;

        SimpleForm.Builder builder = SimpleForm.builder().title("WayStones").content("Wähle einen Waystone aus!");

        SerializableWayStones wayStones = PaperWayStones.plugin.getSerializableWayStones(player.getWorld());
        HashMap<String, Integer> hashMap = new HashMap<>();

        if (favoriteSelectionMode)  builder.button("§3★ Favoriten speichern ★");
        else builder.button("§6★ Favoriten ändern ★");


        for (Map.Entry<Integer, Location> entry : wayStones.getWayStones().entrySet()) {
            Integer integer = entry.getKey();
            Location location = entry.getValue();
            CustomBlockData customBlockData = new CustomBlockData(location.getBlock(), PaperWayStones.plugin);
            SerializableWayStone wayStone = customBlockData.get(PaperWayStones.WAYSTONE_KEY, WayStoneDataTypes.WAY_STONE);
            if(wayStone != null){
                String color = "§r";
                if (player.getPersistentDataContainer().has(new NamespacedKey("waystones", "favorite_waystones"))) {
                    for (int id : player.getPersistentDataContainer().get(new NamespacedKey("waystones", "favorite_waystones"), PersistentDataType.INTEGER_ARRAY)) {
                        if (id == integer) {
                            color = "§6";
                        }
                    }
                }
                String text = ( color + "[★]  §r" + integer+" - "+wayStone.getName() );
                hashMap.put(text, integer);
                builder.button(text);
            }
        }

        builder.responseHandler((form, responseData) -> {
            
            SimpleFormResponse response = form.parseResponse(responseData);
            ButtonComponent button = response.getClickedButton();
            
            if(button != null){
                if (hashMap.containsKey(button.getText())) {
                    if (favoriteSelectionMode) player.chat("/waystone favorite " + hashMap.get(button.getText()));
                    else player.chat("/waystone tp " + hashMap.get(button.getText()));
                }
                else {
                    player.chat("/waystone favoriteselectionmenu");
                }
            }
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
