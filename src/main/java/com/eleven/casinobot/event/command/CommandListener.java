package com.eleven.casinobot.event.command;

import com.eleven.casinobot.core.annotations.EventHandler;
import com.eleven.casinobot.core.command.CommandContext;
import com.eleven.casinobot.core.command.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@EventHandler(commands = PingCommand.class)
public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull
                                              SlashCommandInteractionEvent event) {
        CommandManager.invoke(this, event.getName(), new CommandContext(event));
    }
}
