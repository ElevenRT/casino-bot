package com.eleven.casinobot.event.test;

import com.eleven.casinobot.database.DatabaseTemplate;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.data.MemberDAO;
import com.eleven.casinobot.event.annotations.EventHandler;
import com.eleven.casinobot.event.annotations.Injection;

/**
 * do not use in real
 * this class is for test
 */
@Deprecated
@EventHandler
public class TestListener {

    @Injection(name = "MemberDAO")
    @SuppressWarnings("unused")
    private MemberDAO memberDAO;

    public DatabaseTemplate<Member, Long> getMemberDAO() {
        return memberDAO;
    }
}
