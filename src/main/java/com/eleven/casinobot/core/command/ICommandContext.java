package com.eleven.casinobot.core.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * The ICommandContext interface is an interface designed to ensure
 * that command events and guilds are implemented.
 * @see SlashCommandInteractionEvent
 * @author iqpizza6349
 */
public interface ICommandContext {

    /**
     * Returns guild information of the SlashCommandInteractionEvent that occurred.
     * @return Guild information for events that occurred
     */
    Guild getGuild();

    /**
     * Returns the overall information of the slash command InteractionEvent.
     * @return Information about the event that occurred
     */
    SlashCommandInteractionEvent getEvent();
}
