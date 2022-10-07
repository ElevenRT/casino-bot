package com.eleven.casinobot.core.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/**
 * The IBotContext interface is an interface designed to ensure
 * that bot events and guilds ae implemented;
 * @param <Event> is must be implements {@link GenericInteractionCreateEvent}
 * @see GenericInteractionCreateEvent
 * @author iqpizza6349
 */
public interface IBotContext<Event extends GenericInteractionCreateEvent> {

    /**
     * Returns guild information of the Event that occurred.
     * @return Guild information for events that occurred
     */
    Guild getGuild();

    /**
     * Returns the overall information of the event.
     * @return Information about the event that occurred
     */
    Event getEvent();
}
