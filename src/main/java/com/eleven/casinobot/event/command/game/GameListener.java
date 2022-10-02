package com.eleven.casinobot.event.command.game;

import com.eleven.casinobot.core.annotations.EventHandler;
import com.eleven.casinobot.core.command.CommandManager;
import com.eleven.casinobot.core.game.GameContext;
import com.eleven.casinobot.core.game.GameManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@EventHandler(commands = RouletteCommand.class)
public class GameListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull
                                              SlashCommandInteractionEvent event) {
        CommandManager.invoke(this, event.getName(),
                new GameContext(event, event.getName()));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        GameManager.invokeInteraction(event);
    }
}
