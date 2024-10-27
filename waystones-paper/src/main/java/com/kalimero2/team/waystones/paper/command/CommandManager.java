package com.kalimero2.team.waystones.paper.command;

import com.google.common.collect.ImmutableList;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CommandManager {

    public CommandManager(final PaperWayStones wayStones) throws Exception {
        final LegacyPaperCommandManager<CommandSender> manager = new LegacyPaperCommandManager<>(
                wayStones,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );


        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }


        ImmutableList.of(
                new WayStoneCommands(wayStones, manager)
        ).forEach(CommandHandler::register);


    }
}
