package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;


public class FloodgateIntegration {
    private final PaperWayStones plugin;

    public FloodgateIntegration(PaperWayStones plugins) {
        this.plugin = plugins;
    }

    public void showBedrockForm(Player player) {

        SimpleForm.Builder builder = SimpleForm.builder().title("WayStones").content("Wähle einen Waystone aus!");

        StoredWaystone[] waystones = plugin.getStorage().getWaystones(player.getWorld().getUID());
        for (StoredWaystone waystone : waystones) {
            builder.button(waystone.name());
        }

        builder.validResultHandler(simpleFormResponse -> {
            int clickedButtonId = simpleFormResponse.clickedButtonId();
            StoredWaystone waystone = waystones[clickedButtonId];
            player.chat("/waystone tp " + waystone.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());

    }

}
