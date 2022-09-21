package com.eleven.casinobot.core.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommandContext {
    Guild getGuild();
    SlashCommandInteractionEvent getEvent();
}
