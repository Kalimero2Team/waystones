package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GeyserWaystoneHackCompat implements Listener {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    public GeyserWaystoneHackCompat(PaperWayStones plugin) {
        this.plugin = plugin;

        this.manager = plugin.getManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getChunk();
        hackWaystones(player, chunk);
    }

    public void hackWaystones(Player player, Chunk chunk) {
        if (plugin.isBedrockPlayer(player)) {
            manager.getWaystones(chunk).forEach(waystone -> sendBedrockWaystoneBlock(player, waystone));
        }
    }

    public static void sendBedrockWaystoneBlock(Player player, StoredWaystone waystone){
        Slab blockData = (Slab) Material.PETRIFIED_OAK_SLAB.createBlockData();
        blockData.setType(Slab.Type.DOUBLE);
        player.sendBlockChange(waystone.location(), blockData);
    }




}
