package com.eleven.casinobot.config;

import com.eleven.casinobot.event.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

/**
 * A class for setting up the bot Cache.
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class BotConfig {

    private BotConfig() {}

    /**
     * initialize setting bots cache
     * @return running bot source
     * @throws LoginException when bot token is invalid
     */
    public static JDA initBot() throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(AppConfig.getToken());

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.playing("카드 재정렬"));
        builder.addEventListeners(new ReadyListener());
        return builder.build();
    }
}
