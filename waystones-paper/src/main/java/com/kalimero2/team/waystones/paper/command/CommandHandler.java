package com.kalimero2.team.waystones.paper.command;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public abstract class CommandHandler {
    protected final PaperWayStones plugin;
    protected final LegacyPaperCommandManager<CommandSender> commandManager;

    protected CommandHandler(PaperWayStones wayStonesPlugin, LegacyPaperCommandManager<CommandSender> commandManager) {
        this.plugin = wayStonesPlugin;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
