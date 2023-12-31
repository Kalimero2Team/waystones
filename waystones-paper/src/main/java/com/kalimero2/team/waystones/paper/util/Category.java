package com.kalimero2.team.waystones.paper.util;

import org.bukkit.entity.Player;

public record Category(int id, String name, boolean isPublic) {
    public static Category NONE = new Category(-1, "None", true);

    public boolean usableBy(Player player) {
        return isPublic || player.hasPermission("waystones.category");
    }
}
