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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.*;

@Command(value = "roulette", description = "Casino Queen, Roulette",
        detail =
        @CommandDetail(
                commandType = CommandDetail.Type.GAME,
                gameType = "ROULETTE",
                interactionId = "roulette_join"
        )
)
public class RouletteCommand implements ICommand, IButtonInteraction, IMenuInteraction {
    private static final String GAME_TYPE = "ROULETTE";

    private static final Set<Long> INTERACTIONS = new HashSet<>();


    private static final Map<String, String> ROULETTE_DATA = new LinkedHashMap<>();
    private static final Emoji[] EMOJIS = {
            Emoji.fromUnicode("U+1F947"), Emoji.fromUnicode("U+1F50D"),
            Emoji.fromUnicode("U+0023 U+FE0F U+20E3"), Emoji.fromUnicode("U+1F948"),
            Emoji.fromUnicode("U+2B1B"), Emoji.fromUnicode("U+1F7E5"),
            Emoji.fromUnicode("U+1F949"), Emoji.fromUnicode("U+002A U+FE0F U+20E3"),
            Emoji.fromUnicode("U+1F50E"), Emoji.fromUnicode("U+1F3F4"),
            Emoji.fromUnicode("U+1F3F3"), Emoji.fromUnicode("U+1F3F4 U+200D U+2620 U+FE0F")
    };
    private static final List<String> LABELS;
    private static final Set<SelectOption> SELECT_OPTIONS = new LinkedHashSet<>();
    private static final Random RANDOM = new Random();

    static {
        ROULETTE_DATA.put("1st", "1st");
        ROULETTE_DATA.put("1-18", "1to18");
        ROULETTE_DATA.put("EVEN", "even");
        ROULETTE_DATA.put("2nd", "2nd");
        ROULETTE_DATA.put("BLACK", "black");
        ROULETTE_DATA.put("RED", "red");
        ROULETTE_DATA.put("3rd", "3rd");
        ROULETTE_DATA.put("ODD", "odd");
        ROULETTE_DATA.put("19-36", "19to36");
        ROULETTE_DATA.put("2 to 1 (1, 4, 7, ...34)", "index1");
        ROULETTE_DATA.put("2 to 1 (2, 5, 8, ...34)", "index2");
        ROULETTE_DATA.put("2 to 1 (3, 6, 9, ...34)", "index3");
        LABELS = new LinkedList<>(ROULETTE_DATA.keySet());
        Iterator<Map.Entry<String, String>> iterator = ROULETTE_DATA.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            SELECT_OPTIONS.add(SelectOption.of(key, value).withEmoji(EMOJIS[i++]));
        }
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
                EmbedBuilder builder = new EmbedBuilder();
                event.replyEmbeds(
                        builder
                                .setTitle("The ball is thrown. Where will arrive?")
                                .setThumbnail("https://raw.githubusercontent.com/ElevenRT/images/main/roulette-unscreen%20(1).gif")
                                .setDescription(String.format("The Ball has arrived in %s", LABELS.get(RANDOM.nextInt(LABELS.size()))))
                                .build()
                ).queue();
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
                                    .addOptions(SELECT_OPTIONS).build()
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
