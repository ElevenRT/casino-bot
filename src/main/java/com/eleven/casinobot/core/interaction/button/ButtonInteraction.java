package com.eleven.casinobot.core.interaction.button;

import com.eleven.casinobot.core.context.ComponentContextSingleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;

public final class ButtonInteraction {
    private static final Map<Long, String> GAME_PLAYERS = new HashMap<>();
    private static final Map<String, Set<String>> GAME_TYPES_IDS = new HashMap<>();
    private static final Map<Guild, Map<String, State>> GUILD_GAMES = new HashMap<>();
    private static final String SKIP_EMOJI = "⏩";
    private static final String STOP_EMOJI = "⛔";

    static {
        try {
            GAME_TYPES_IDS.putAll(ComponentContextSingleton.getInstance()
                    .getAllGameInfos());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(GAME_TYPES_IDS);
    }

    private ButtonInteraction() {}

    public static void init() {
        // Nothing .
        // just for game type emojis map initialize
    }

    public static void invokeInteraction(ButtonInteractionEvent event) {
        // GameManager 애서 버튼 자료를 검색하고 @CommandDetail 에 있는 데이터들을 사용하여 처리함
        // skip 버튼인지 종료 버튼인지 먼저 검사한 후 게임 종류를 검색한다.
        Guild guild = event.getGuild();
        Emoji emoji = event.getButton().getEmoji();
        User user = event.getUser();
        if (emoji == null || user.isBot() || user.isSystem()) {
            // 게임에서는 emoji 가 빠져서는 안된다.
            // 사실 어떻게 구현하든 자기 마음이지만, 최소한 여기서는 아니다.
            return;
        }

        List<ActionRow> actionRows = event.getMessage().getActionRows();
        String gameType = null;
        for (ActionRow actionRow : actionRows) {
            List<Button> buttons = actionRow.getButtons();
            for (Button button : buttons) {
                gameType = findGameTypeByComponentId(button.getId());
                if (gameType != null) {
                    break;
                }
            }

            if (gameType != null) {
                break;
            }
        }

        if (gameType == null) {
            return;
        }

        GUILD_GAMES.computeIfAbsent(guild, k -> new HashMap<>());
        if (emoji.getName().equals(SKIP_EMOJI)) {
            GUILD_GAMES.get(guild).put(gameType, State.SKIP);
            event.reply("Skip waiting for player").setEphemeral(true)
                    .queue();
        }
        else if (emoji.getName().equals(STOP_EMOJI)) {
            GUILD_GAMES.get(guild).put(gameType, State.STOP);
            event.reply(String.format("Stop playing game: %s", gameType))
                    .setEphemeral(true)
                    .queue();
        }
        else {
            GUILD_GAMES.get(guild).put(gameType, State.PLAY);

            if (GAME_PLAYERS.get(event.getUser().getIdLong()) != null) {
                return;
            }

            GAME_PLAYERS.put(user.getIdLong(), gameType);
            event.reply("game start").setEphemeral(true)
                    .queue();
        }
    }

    private static String findGameTypeByComponentId(String componentId) {
        String gameType = null;
        boolean found = false;
        for (Map.Entry<String, Set<String>> gameId : GAME_TYPES_IDS.entrySet()) {
            Set<String> ids = gameId.getValue();
            for (String id : ids) {
                if (id.equals(componentId)) {
                    gameType = gameId.getKey();
                    found = true;
                    break;
                }
            }

            if (found) {
                break;
            }
        }

        return gameType;
    }

    public static Set<Long> getPlayersByGameType(String gameType) {
        Set<Long> players = new LinkedHashSet<>();
        for (Map.Entry<Long, String> player : GAME_PLAYERS.entrySet()) {
            if (!player.getValue().equals(gameType)) {
                continue;
            }

            players.add(player.getKey());
        }

        return players;
    }

    static boolean isSkip(Guild guild, String gameType) {
        return GUILD_GAMES.getOrDefault(guild, new HashMap<>())
                .get(gameType) == State.SKIP;
    }

    static boolean isStop(Guild guild, String gameType) {
        return GUILD_GAMES.getOrDefault(guild, new HashMap<>())
                .get(gameType) == State.STOP;
    }

    enum State {
        PLAY, SKIP, STOP
    }
}
