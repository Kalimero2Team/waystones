package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Waystone;
import com.kalimero2.team.waystones.paper.util.FakeArmorStandBuilder;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import net.kyori.adventure.text.Component;
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
            Location baseLocation = new Location(plugin.getServer().getWorld(waystone.world()), waystone.block_x(), waystone.block_y(), waystone.block_z());
            player.sendBlockChange(baseLocation, Material.BARRIER.createBlockData());
            player.sendBlockChange(baseLocation.clone().add(0,1,0), Material.BARRIER.createBlockData());
            FakeArmorStandBuilder fakeArmorStandBuilder = new FakeArmorStandBuilder();
            fakeArmorStandBuilder.setLocation(baseLocation.clone().toCenterLocation().add(0, -0.5, 0));
            fakeArmorStandBuilder.setName(Component.text(waystone.name()));
            fakeArmorStandBuilder.setVisible(false);
            fakeArmorStandBuilder.setShowName(true);
            fakeArmorStandBuilder.setHeadItem(plugin.getItem());
            fakeArmorStandBuilder.createFakeArmorStand().showForPlayer(player);
        }
    }



}
