package com.eleven.casinobot.event.command;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.interaction.command.ICommand;
import com.eleven.casinobot.core.interaction.command.ICommandContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(value = "ping", description = "Calculate ping of the Bot")
public class PingCommand implements ICommand {

    @Override
    public void onEvent(ICommandContext ctx) {
        SlashCommandInteractionEvent event = ctx.getEvent();
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(hook ->
                        hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time))
                .queue();
    }
}
