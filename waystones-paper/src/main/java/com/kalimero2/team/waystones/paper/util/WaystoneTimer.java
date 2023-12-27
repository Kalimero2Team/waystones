package com.kalimero2.team.waystones.paper.util;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.Bukkit;

public class WaystoneTimer {

    private final PaperWayStones plugin;

    public WaystoneTimer(PaperWayStones plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::check, 0, 288000); // 4h * 60m * 60s * 20t
    }

    public void check() {
        long last = plugin.getConfig().getLong("last-uses-decrement", 0);
        if (System.currentTimeMillis() - last < 86400000) { // 24h * 60m * 60s * 1000ms
            return;
        }
        plugin.getLogger().info("Decreasing waystone use scores... \n This action may take a while");
        plugin.getManager().decreaseGlobalUsesScore();
        plugin.getConfig().set("last-uses-decrement", System.currentTimeMillis());
        plugin.saveConfig();
        plugin.getLogger().info("Waystone use scores were decreased");
    }
}
