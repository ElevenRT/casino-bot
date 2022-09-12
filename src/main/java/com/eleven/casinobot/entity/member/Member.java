package com.eleven.casinobot.entity.member;

import java.time.LocalDateTime;

public class Member {

    private final Long userId;
    private Long money;
    private MemberType memberType;
    private LocalDateTime brokenAt;
    private Boolean broken;

    public static Member.Builder builder() {
        return new Member.Builder();
    }

    public Member(Long userId) {
        this(userId, null);
    }

    public Member(Long userId, Long money) {
        this(userId, money, null);
    }

    public Member(Long userId, Long money, MemberType memberType) {
        this(userId, money, memberType, null);
    }

    public Member(Long userId, Long money, MemberType memberType,
                  LocalDateTime brokenAt) {
        this(userId, money, memberType, brokenAt, null);
    }

    public Member(Long userId, Long money, MemberType memberType,
                  LocalDateTime brokenAt, Boolean broken) {
        this.userId = userId;
        this.money = money;
        this.memberType = memberType;
        this.brokenAt = brokenAt;
        this.broken = broken;
    }

    public static class Builder {
        private Long userId;
        private Long money;
        private MemberType memberType;
        private LocalDateTime brokenAt;
        private Boolean broken;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder money(Long money) {
            this.money = money;
            return this;
        }

        public Builder memberType(MemberType memberType) {
            this.memberType = memberType;
            return this;
        }

        public Builder brokenAt(LocalDateTime brokenAt) {
            this.brokenAt = brokenAt;
            return this;
        }

        public Builder broken(Boolean broken) {
            this.broken = broken;
            return this;
        }

        public Member build() {
            return new Member(this);
        }
    }

    public Member(Builder builder) {
        this.userId = builder.userId;
        this.money = builder.money;
        this.memberType = builder.memberType;
        this.brokenAt = builder.brokenAt;
        this.broken = builder.broken;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMoney() {
        return money;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public LocalDateTime getBrokenAt() {
        return brokenAt;
    }

    public Boolean isBroken() {
        return broken;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void setBrokenAt(LocalDateTime brokenAt) {
        this.brokenAt = brokenAt;
    }

    public void setBroken(Boolean broken) {
        this.broken = broken;
    }
}
