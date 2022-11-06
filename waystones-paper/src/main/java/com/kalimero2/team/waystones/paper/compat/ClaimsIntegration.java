package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.claims.api.ClaimsApi;
import com.kalimero2.team.claims.api.ClaimsChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class ClaimsIntegration {
    public static boolean shouldCancel(Chunk chunk, Player player) {
        ClaimsChunk claimsChunk = ClaimsApi.getApi().getChunk(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
        if(claimsChunk.isClaimed()){
            if(claimsChunk.hasOwner()){
                if(!claimsChunk.getOwner().equals(player.getUniqueId())){
                    return !claimsChunk.isTrusted(player.getUniqueId());
                }
            }else {
                return !player.hasPermission("claims.admin.teamclaim");
            }
        }
        return false;
    }
}
