package com.eleven.casinobot.event.test;

import com.eleven.casinobot.database.DatabaseTemplate;
import com.eleven.casinobot.entity.member.Member;
import com.eleven.casinobot.entity.member.data.MemberDAO;
import com.eleven.casinobot.event.annotations.EventListener;
import com.eleven.casinobot.event.annotations.Inject;

/**
 * do not use in real
 * this class is for test
 */
@Deprecated
@EventListener
public class TestListener {

    @Inject(name = "MemberDAO")
    private MemberDAO memberDAO;

    public DatabaseTemplate<Member, Long> getMemberDAO() {
        return memberDAO;
    }
}
