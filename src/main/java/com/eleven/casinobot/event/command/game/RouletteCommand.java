package com.eleven.casinobot.event.command.game;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.annotations.CommandDetail;
import com.eleven.casinobot.core.command.ICommand;
import com.eleven.casinobot.core.command.ICommandContext;
import com.eleven.casinobot.core.game.IGameContext;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Command(value = "roulette", description = "Casino Queen, Roulette",
        detail =
        @CommandDetail(
                commandType = CommandDetail.Type.GAME,
                gameType = "ROULETTE",
                interactionIds = "roulette_join"
        )
)
public class RouletteCommand implements ICommand {

    @Override
    public void onEvent(ICommandContext ctx) {
        IGameContext context = (IGameContext) ctx;
        SlashCommandInteractionEvent event = context.getEvent();

        event.reply("roulette").setEphemeral(false)
                .addActionRow(
                        Button.primary("roulette_join", Emoji.fromUnicode("U+2714")),
                        Button.success("roulette_skip", Emoji.fromUnicode("U+23E9")),
                        Button.danger("roulette_stop", Emoji.fromUnicode("U+26D4"))
                ).queue();
    }
}
