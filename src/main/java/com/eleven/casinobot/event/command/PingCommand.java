package com.eleven.casinobot.event.command;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.command.CommandContext;
import com.eleven.casinobot.core.command.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Command(value = "ping", description = "Calculate ping of the Bot")
public class PingCommand implements ICommand {

    @Override
    public void onEvent(CommandContext ctx) {
        SlashCommandInteractionEvent event = ctx.getEvent();
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(hook ->
                        hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time))
                        .queue();
    }
}
