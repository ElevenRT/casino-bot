package com.eleven.casinobot;

import com.eleven.casinobot.config.BotConfig;
import net.dv8tion.jda.api.JDA;

import javax.security.auth.login.LoginException;

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = BotConfig.initBot();
        jda.awaitReady();
    }
}
