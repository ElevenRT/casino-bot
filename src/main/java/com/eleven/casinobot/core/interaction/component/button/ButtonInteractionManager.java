package com.eleven.casinobot.core.interaction.component.button;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.annotations.CommandDetail;
import com.eleven.casinobot.core.context.ComponentContextSingleton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ButtonInteractionManager {

    /**
     * PrimaryInteractionData(key) 는 게임을 요청한 유저 id 와 게임 종류
     * Set(Long/value) 은 모든 참가자들의 고유 id(게임 요청한 유저 포함)
     */
    private static final Map<PrimaryInteractionData, Set<Long>> INTERACTIONERS = new HashMap<>();
    
    /**
     * String(key) 은 게임 이름(종류)
     * String(value) 은 게임 아아디(식별 컴포넌트 아이디)
     */
    private static final Map<String, String> TYPES_IDS = new HashMap<>();
    
    private static final Map<Class<?>, Object> BUTTON_INTERACTIONS = new HashMap<>();

    private static final String SKIP_EMOJI = "⏩";
    private static final String STOP_EMOJI = "⛔";

    static {
        try {
            TYPES_IDS.putAll(ComponentContextSingleton.getInstance()
                    .getAllInteractionInfos());
            BUTTON_INTERACTIONS.putAll(ComponentContextSingleton.getInstance()
                    .getAllInteractions());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private ButtonInteractionManager() {}

    public static void init() {
        // Nothing .
        // just for game type emojis map initialize
    }

    public static void invokeInteraction(ButtonComponentContext ctx) {
        // GameManager 애서 버튼 자료를 검색하고 @CommandDetail 에 있는 데이터들을 사용하여 처리함
        // skip 버튼인지 종료 버튼인지 먼저 검사한 후 게임 종류를 검색한다.
        Guild guild = ctx.getGuild();
        ButtonInteractionEvent event = ctx.getEvent();
        Button button = event.getButton();
        Emoji emoji = button.getEmoji();
        User user = event.getUser();
        if (emoji == null || user.isBot() || user.isSystem() || button.getId() == null) {
            // 게임에서는 emoji 가 빠져서는 안된다.
            // 사실 어떻게 구현하든 자기 마음이지만, 최소한 여기서는 아니다.
            return;
        }

        PrimaryInteractionData interactionData = null;
        for (PrimaryInteractionData data : INTERACTIONERS.keySet()) {
            if (data.guild.equals(guild)) {
                interactionData = data;
            }
        }

        if (interactionData == null) {
            event.reply("there is no type has create on this guild").queue();
        }
        else {
            List<ActionRow> actionRows = event.getMessage().getActionRows();
            String gameType = null;
            for (ActionRow actionRow : actionRows) {
                List<Button> buttons = actionRow.getButtons();
                for (Button b : buttons) {
                    gameType = findGameTypeByComponentId(b.getId());
                    if (gameType != null) {
                        break;
                    }
                }

                if (gameType != null) {
                    break;
                }
            }

            if (gameType == null) {
                invokeButtonInteractionAll(ctx);
            }
            else {
                if (emoji.getName().equals(SKIP_EMOJI) && button.getId().equals("SKIP")) {
                    invokeButtonInteraction(gameType, ctx);
                } else if (emoji.getName().equals(STOP_EMOJI) && button.getId().equals("STOP")) {
                    event.reply(String.format("Stop playing game: %s", gameType))
                            .setEphemeral(true)
                            .queue();
                } else {
                    boolean alreadyParticipated = isParticipated(user.getIdLong());
                    if (alreadyParticipated) {
                        event.reply("You have already participated.").setEphemeral(true).queue();
                    } else {
                        INTERACTIONERS.get(interactionData).add(user.getIdLong());
                        event.reply("Participated in the game.").setEphemeral(true).queue();
                    }
                }
            }
        }
    }

    private static String findGameTypeByComponentId(String componentId) {
        String gameType = null;
        for (Map.Entry<String, String> gameId : TYPES_IDS.entrySet()) {
            String id = gameId.getValue();
            if (id.equals(componentId)) {
                gameType = gameId.getKey();
                break;
            }
        }

        return gameType;
    }

    public static boolean createType(long userId, Guild guild, String type) {
        if (isParticipated(userId)) {
            return false;
        }

        Set<Long> longs = new HashSet<>();
        longs.add(userId);
        INTERACTIONERS.put(new PrimaryInteractionData(userId, type, guild), longs);
        return true;
    }

    public static Set<Long> getInteractioners(Guild guild, String type) {
        PrimaryInteractionData interactionData = getByGuildAndType(guild, type);
        if (interactionData == null) {
            return Collections.emptySet();
        }

        return INTERACTIONERS.get(interactionData);
    }

    private static PrimaryInteractionData getByGuildAndType(Guild guild, String type) {
        for (PrimaryInteractionData data : INTERACTIONERS.keySet()) {
            if (data.guild.equals(guild) && data.type.equals(type)) {
                return data;
            }
        }

        return null;
    }

    private static boolean isParticipated(long id) {
        for (Set<Long> ids : INTERACTIONERS.values()) {
            if (ids.contains(id)) {
                return true;
            }
        }

        return false;
    }

    private static void invokeButtonInteraction(String type,
                                                IButtonComponentContext context) {
        for (Map.Entry<Class<?>, Object> button : BUTTON_INTERACTIONS.entrySet()) {
            CommandDetail detail = button.getKey().getAnnotation(Command.class).detail();
            if (detail.gameType().equals(type)) {
                ((IButtonInteraction) button.getValue()).onButtonEvent(context);
            }
        }
    }

    private static void invokeButtonInteractionAll(IButtonComponentContext context) {
        for (Map.Entry<Class<?>, Object> button : BUTTON_INTERACTIONS.entrySet()) {
            if (button.getValue() instanceof IButtonInteraction) {
                ((IButtonInteraction) button.getValue()).onButtonEvent(context);
            }
        }
    }

    static class PrimaryInteractionData {
        private final long userId;
        private final String type;
        private final Guild guild;

        public PrimaryInteractionData(long userId, String type, Guild guild) {
            this.userId = userId;
            this.type = type;
            this.guild = guild;
        }
    }
}
