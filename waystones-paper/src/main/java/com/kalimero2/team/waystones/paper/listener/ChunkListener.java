package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Waystone;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChunkListener implements Listener {


    private final PaperWayStones plugin;

    public ChunkListener(PaperWayStones plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onChunkSend(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getChunk();

        Waystone[] waystones = plugin.getStorage().getWaystones(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
        for (Waystone waystone : waystones) {
            player.sendBlockChange(new Location(plugin.getServer().getWorld(waystone.world()), waystone.block_x(), waystone.block_y(), waystone.block_z()), Material.DIAMOND_BLOCK.createBlockData());
        }
    }



}
