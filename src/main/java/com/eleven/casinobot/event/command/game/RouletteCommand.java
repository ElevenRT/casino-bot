package com.eleven.casinobot.event.command.game;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.annotations.CommandDetail;
import com.eleven.casinobot.core.interaction.command.ICommand;
import com.eleven.casinobot.core.interaction.command.ICommandContext;
import com.eleven.casinobot.core.interaction.component.button.ButtonInteractionManager;
import com.eleven.casinobot.core.interaction.component.button.IButtonInteraction;
import com.eleven.casinobot.core.interaction.component.button.IButtonComponentContext;
import com.eleven.casinobot.core.interaction.component.menu.IMenuComponentContext;
import com.eleven.casinobot.core.interaction.component.menu.IMenuInteraction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Command(value = "roulette", description = "Casino Queen, Roulette",
        detail =
        @CommandDetail(
                commandType = CommandDetail.Type.GAME,
                gameType = "ROULETTE",
                interactionId = "roulette_join"
        )
)
public class RouletteCommand implements ICommand, IButtonInteraction, IMenuInteraction {

    private static final Logger log = LoggerFactory.getLogger(RouletteCommand.class);

    private static final String GAME_TYPE = "ROULETTE";

    private static final Set<Long> INTERACTIONS = new HashSet<>();

    private static final String ROULETTE_ANIMATION;

    static {
        ClassLoader classLoader = RouletteCommand.class.getClassLoader();
        final URL url  = classLoader.getResource("gifs/roulette.gif");
        if (url == null) {
            throw new RuntimeException("file not found");
        }
        ROULETTE_ANIMATION = url.getFile().replaceFirst("/", "")
                .replaceAll("/", "\\\\");
    }

    @Override
    public void onCommandEvent(ICommandContext ctx) {
        SlashCommandInteractionEvent event = ctx.getEvent();
        User user = event.getUser();
        if (!ButtonInteractionManager.createType(user.getIdLong(), event.getGuild(), GAME_TYPE)) {
            event.reply("Users participating in the game cannot create games.")
                    .setEphemeral(true).queue();
        }
        else {
            event.reply("roulette").setEphemeral(false)
                    .addActionRow(
                            Button.primary("roulette_join", Emoji.fromUnicode("U+2714")),
                            Button.success("SKIP", Emoji.fromUnicode("U+23E9")),
                            Button.danger("STOP", Emoji.fromUnicode("U+26D4"))
                    ).queue();
        }
    }

    @Override
    public void onButtonEvent(IButtonComponentContext ctx) {
        ButtonInteractionEvent event = ctx.getEvent();
        Button button = event.getButton();
        EmojiUnion emoji = button.getEmoji();
        assert emoji != null;
        if (emoji.asUnicode().getAsCodepoints().equalsIgnoreCase("U+1F340")) {
            if (INTERACTIONS.containsAll(getInteractioners(ctx.getGuild(), "ROULETTE"))) {
                File file = new File(ROULETTE_ANIMATION);
                log.info("{}", file.exists());
                try {
                    event.replyFiles(FileUpload.fromData(file)).complete(true);
                } catch (RateLimitedException e) {
                    JDA jda = event.getJDA();
                    jda.shutdownNow();
                    while (jda.getStatus() != JDA.Status.SHUTDOWN) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
            else {
                event.reply("돌아가").queue();
            }
        }
        else {
            event.reply("Please select a place to bet . (Straight bets are not allowed.)")
                    .addActionRow(
                            SelectMenu.create(GAME_TYPE + "_TABLE")
                                    .addOption("0", "0", Emoji.fromUnicode("U+0030 U+FE0F U+20E3"))
                                    .addOptions(
                                            SelectOption.of("1st", "1st").withEmoji(Emoji.fromUnicode("U+1F947")),
                                            SelectOption.of("1-18", "1to18").withEmoji(Emoji.fromUnicode("U+1F50D")),
                                            SelectOption.of("EVEN", "even").withEmoji(Emoji.fromUnicode("U+0023 U+FE0F U+20E3")),
                                            SelectOption.of("2nd", "2nd").withEmoji(Emoji.fromUnicode("U+1F948")),
                                            SelectOption.of("BLACK", "black").withEmoji(Emoji.fromUnicode("U+2B1B")),
                                            SelectOption.of("RED", "red").withEmoji(Emoji.fromUnicode("U+1F7E5")),
                                            SelectOption.of("3rd", "3rd").withEmoji(Emoji.fromUnicode("U+1F949")),
                                            SelectOption.of("ODD", "odd").withEmoji(Emoji.fromUnicode("U+002A U+FE0F U+20E3")),
                                            SelectOption.of("19-36", "19to36").withEmoji(Emoji.fromUnicode("U+1F50E")),
                                            SelectOption.of("2 to 1 (1, 4, 7, .. 34)", "index1").withEmoji(Emoji.fromUnicode("U+1F3F4")),
                                            SelectOption.of("2 to 1 (2, 5, 8, .. 35)", "index2").withEmoji(Emoji.fromUnicode("U+1F3F3")),
                                            SelectOption.of("2 to 1 (3, 6, 9, .. 36)", "index3").withEmoji(Emoji.fromUnicode("U+1F3F4 U+200D U+2620 U+FE0F"))
                                    ).build()
                    ).addActionRow(Button.of(ButtonStyle.PRIMARY, GAME_TYPE + "_END", "SEE RESULT", Emoji.fromUnicode("U+1F340")))
                    .queue();
        }
    }

    @Override
    public void onMenuEvent(IMenuComponentContext ctx) {
        System.out.println(ctx);
        INTERACTIONS.add(ctx.getEvent().getUser().getIdLong());
        ctx.getEvent().reply("선택하였습니다.").setEphemeral(true).queue();
    }

    @Override
    public Set<Long> getInteractioners(Guild guild, String type) {
        return ButtonInteractionManager.getInteractioners(guild, type);
    }
}
