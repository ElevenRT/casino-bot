package com.eleven.casinobot.mapper.game;

public class Game {

    private final Integer gameId;

    private final GameType gameType;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer gameId;
        private GameType gameType;

        public Builder gameId(Integer gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder gameType(GameType gameType) {
            this.gameType = gameType;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    public Game(Builder builder) {
        this.gameId = builder.gameId;
        this.gameType = builder.gameType;
    }

    public Integer getGameId() {
        return gameId;
    }

    public GameType getGameType() {
        return gameType;
    }
}
