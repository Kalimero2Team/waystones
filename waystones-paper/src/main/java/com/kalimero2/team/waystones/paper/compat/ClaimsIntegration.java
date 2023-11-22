package com.kalimero2.team.waystones.paper.compat;

import com.kalimero2.team.claims.api.Claim;
import com.kalimero2.team.claims.api.ClaimsApi;
import com.kalimero2.team.claims.api.group.Group;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class ClaimsIntegration {

    private final ClaimsApi api;
    public ClaimsIntegration() {
        this.api = ClaimsApi.getApi();
    }

    public boolean shouldCancel(Chunk bukkitChunk, Player player) {
        return shouldCancel(player, api.getClaim(bukkitChunk));
    }

    private boolean shouldCancel(Player player, Claim claim) {
        if (claim != null) {
            List<Group> members = claim.getMembers();

            Optional<Group> any = members.stream().filter(group -> api.getGroupMember(group, player) != null).findAny();
            return any.isEmpty();
        }
        return false;
    }

}
