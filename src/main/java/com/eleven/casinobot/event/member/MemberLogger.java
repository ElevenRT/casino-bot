package com.eleven.casinobot.event.member;

import com.eleven.casinobot.event.annotations.EventHandler;
import com.eleven.casinobot.event.annotations.Injection;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.data.MemberDAO;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventHandler
@SuppressWarnings("unused")
public class MemberLogger extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MemberLogger.class);

    @Injection
    private MemberDAO memberDAO;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User author = event.getAuthor();
        if (author.isBot() || author.isSystem()) {
            return;
        }

        Long userId = author.getIdLong();
        if (memberDAO.existsById(userId)) {
            return;
        }

        Member member = Member.builder()
                .userId(userId)
                .build();
        memberDAO.save(member);
    }
}
