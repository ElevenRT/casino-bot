package com.eleven.casinobot.core.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * CommandContext class is a wrapper class that inherits ICommandContext
 * and delivers event information more easily to each command class.
 * @see ICommandContext
 * @see SlashCommandInteractionEvent
 * @see Guild
 * @see com.eleven.casinobot.core.annotations.Command
 * @author iqpizza6349
 */
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
