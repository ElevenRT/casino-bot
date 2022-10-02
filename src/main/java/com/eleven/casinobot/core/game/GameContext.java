package com.eleven.casinobot.core.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Set;

public class GameContext implements IGameContext {

    private final SlashCommandInteractionEvent commandEvent;
    private final String gameType;

    public GameContext(SlashCommandInteractionEvent commandEvent,
                       String gameType) {
        this.commandEvent = commandEvent;
        this.gameType = gameType;
    }

    @Override
    public Guild getGuild() {
        return commandEvent.getGuild();
    }

    @Override
    public SlashCommandInteractionEvent getEvent() {
        return commandEvent;
    }

    @Override
    public Set<Long> getPlayers() {
        return GameManager.getPlayersByGameType(gameType);
    }

    @Override
    public boolean isSkip() {
        return GameManager.isSkip(getGuild(), gameType);
    }

    @Override
    public boolean isGameStop() {
        return GameManager.isStop(getGuild(), gameType);
    }
}
