package com.eleven.casinobot.core.interaction.command;

import com.eleven.casinobot.core.context.IBotContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * The ICommandContext interface is an interface designed to ensure
 * that command events and guilds are implemented.
 * @see SlashCommandInteractionEvent
 * @author iqpizza6349
 */
public interface ICommandContext extends IBotContext<SlashCommandInteractionEvent> {
}
