package com.eleven.casinobot.event.command.game;

import com.eleven.casinobot.core.annotations.EventHandler;
import com.eleven.casinobot.core.interaction.command.CommandContext;
import com.eleven.casinobot.core.interaction.command.CommandManager;
import com.eleven.casinobot.core.interaction.component.button.ButtonComponentContext;
import com.eleven.casinobot.core.interaction.component.button.ButtonInteractionManager;
import com.eleven.casinobot.core.interaction.component.menu.MenuComponentContext;
import com.eleven.casinobot.core.interaction.component.menu.MenuInteractionManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@EventHandler(commands = RouletteCommand.class)
public class GameListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull
                                              SlashCommandInteractionEvent event) {
        CommandManager.invoke(this, event.getName(), new CommandContext(event));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonInteractionManager.invokeInteraction(new ButtonComponentContext(event));
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        MenuInteractionManager.invokeInteraction(new MenuComponentContext(event));
    }
}
