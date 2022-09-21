package com.eleven.casinobot.core.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandContext implements ICommandContext {
    private final SlashCommandInteractionEvent event;

    public CommandContext(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public SlashCommandInteractionEvent getEvent() {
        return event;
    }
}
