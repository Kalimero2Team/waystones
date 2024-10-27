package com.kalimero2.team.waystones.paper.command.argument;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.component.TypedCommandComponent;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.UUID;


public final class WaystoneComponent<C> {
    private static final WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();

    public static TypedCommandComponent.Builder<CommandSender, StoredWaystone> of(final String name) {
        return CommandComponent.<CommandSender, StoredWaystone>ofType(StoredWaystone.class, name)
                .parser(StringParser.stringParser().flatMapSuccess(StoredWaystone.class, (commandContext, commandInput) -> {
                    StoredWaystone waystone = null;
                    try {
                        waystone = manager.getWaystone(UUID.fromString(commandInput));
                    } catch (IllegalArgumentException ignored) {

                    }

                    if (waystone == null) {
                        return ArgumentParseResult.failureFuture(new IllegalArgumentException("No Waystone with ID " + commandInput + " exists."));
                    }

                    return ArgumentParseResult.successFuture(waystone);
                }));
    }

}
