package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.Waystone;
import com.kalimero2.team.waystones.paper.util.FakeArmorStand;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ChunkListener implements Listener {

    private final PaperWayStones plugin;

    public ChunkListener(PaperWayStones plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getChunk();

        Waystone[] waystones = plugin.getStorage().getWaystones(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
        for (Waystone waystone : waystones) {
            Location baseLocation = new Location(plugin.getServer().getWorld(waystone.world()), waystone.block_x(), waystone.block_y(), waystone.block_z()).toCenterLocation().add(0, -0.5, 0);
            TextComponent name = Component.text(waystone.name());
            ItemStack headItem = plugin.getItem();
            FakeArmorStand fakeArmorStand = new FakeArmorStand(baseLocation);
            fakeArmorStand.setHeadItem(headItem);
            fakeArmorStand.setName(Component.text("⬤ ").color(NamedTextColor.YELLOW).append(name.color(NamedTextColor.WHITE)));
            fakeArmorStand.setShowName(true);
            fakeArmorStand.setVisible(false);
            fakeArmorStand.showForPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getChunk();


    }


}
