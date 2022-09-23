package com.eleven.casinobot.event.test;

import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import com.eleven.casinobot.core.annotations.Injection;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.data.MemberDAO;
import com.eleven.casinobot.core.annotations.EventHandler;

/**
 * do not use in real
 * this class is for test
 */
@Deprecated
@EventHandler
public class TestListener {

    @SuppressWarnings("unused")
    private final MemberDAO memberDAO;

    @Injection
    @SuppressWarnings("unused")
    private MemberDAO abc;

    public TestListener(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    public AbstractDatabaseTemplate<Member, Long> getMemberDAO() {
        return memberDAO;
    }

    public MemberDAO getAbc() {
        return abc;
    }
}
