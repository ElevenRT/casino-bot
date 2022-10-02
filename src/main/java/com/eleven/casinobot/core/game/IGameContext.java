package com.eleven.casinobot.core.game;

import com.eleven.casinobot.core.command.ICommandContext;

import java.util.Set;

public interface IGameContext extends ICommandContext {

    Set<Long> getPlayers();

    boolean isSkip();

    boolean isGameStop();

}
