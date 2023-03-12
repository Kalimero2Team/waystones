package com.kalimero2.team.waystones.paper.command;

import com.kalimero2.team.waystones.paper.PaperWayStones;

public abstract class CommandHandler {
    protected final PaperWayStones plugin;
    protected final CommandManager commandManager;

    protected CommandHandler(PaperWayStones wayStonesPlugin, CommandManager commandManager) {
        this.plugin = wayStonesPlugin;
        this.commandManager = commandManager;
    }

    public abstract void register();
}
