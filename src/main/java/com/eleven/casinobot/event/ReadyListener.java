package com.eleven.casinobot.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(ReadyListener.class);

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            log.info("CasinoBot is Ready!");
        }
        if (event instanceof ShutdownEvent) {
            log.info("shutdown CasinoBot..");
        }
    }
}
