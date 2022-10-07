package com.eleven.casinobot.config;

import com.eleven.casinobot.core.annotations.Command;
import com.eleven.casinobot.core.interaction.command.CommandManager;
import com.eleven.casinobot.core.interaction.component.button.ButtonInteractionManager;
import com.eleven.casinobot.core.interaction.component.menu.MenuInteractionManager;
import com.eleven.casinobot.event.ReadyListener;
import com.eleven.casinobot.core.context.ComponentContextSingleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Set;

/**
 * A class for setting up the bot Cache.
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class BotConfig {
    private static final Logger log = LoggerFactory.getLogger(BotConfig.class);

    private BotConfig() {}

    /**
     * initialize setting bots cache
     * @return running bot source
     * @throws LoginException when bot token is invalid
     */
    public static JDA initBot() throws LoginException, IllegalAccessException {
        JDABuilder builder = JDABuilder.createDefault(AppConfig.getToken());

        builder.disableCache(CacheFlag.VOICE_STATE);
        builder.enableIntents(
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.MESSAGE_CONTENT
        );
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.playing("카드 재정렬"));
        builder.addEventListeners(new ReadyListener());

        return initializeEventHandlers(builder.build());
    }

    private static JDA initializeEventHandlers(JDA jda) throws IllegalAccessException {
        Set<Object> eventListener = getEventHandlers();
        for (Object listener : eventListener) {
            log.info("add event listener: {}", listener.getClass().getSimpleName());
            jda.addEventListener(listener);
        }

        return initializeCommands(jda);
    }

    private static JDA initializeCommands(JDA jda) {
        Set<Command> commands = getCommands();
        for (Command command : commands) {
            jda.upsertCommand(command.value(), command.description()).queue();
        }

        ButtonInteractionManager.init();
        MenuInteractionManager.init();
        return jda;
    }

    private static Set<Object> getEventHandlers() throws IllegalAccessException {
        return ComponentContextSingleton.getInstance().getAllEventHandler();
    }

    private static Set<Command> getCommands() {
        return CommandManager.commandValues();
    }
}
