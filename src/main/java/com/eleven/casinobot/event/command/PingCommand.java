package com.eleven.casinobot.event.command;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.command.CommandContext;
import com.eleven.casinobot.core.command.ICommand;
import net.dv8tion.jda.api.entities.MessageChannel;

@Command(value = "ping", description = "Calculate ping of the Bot")
public class PingCommand implements ICommand {

    @Override
    public void onEvent(CommandContext ctx) {
        MessageChannel channel = ctx.getEvent().getMessageChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!")
                .queue(response -> response.editMessageFormat("Pong: %d ms",
                        System.currentTimeMillis() - time).queue());
    }
}
