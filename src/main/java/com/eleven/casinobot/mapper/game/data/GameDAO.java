package com.eleven.casinobot.mapper.game.data;

import com.eleven.casinobot.database.DatabaseTemplate;
import com.eleven.casinobot.database.annotations.Database;
import com.eleven.casinobot.mapper.game.Game;
import com.eleven.casinobot.mapper.game.GameType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Database
public class GameDAO extends DatabaseTemplate<Game, Integer> {

    @Override
    protected String saveQuery(Game entity) {
        return format("insert into game(game_type) values (%s);", entity.getGameType());
    }

    @Override
    protected String selectByIdQuery(Integer id) {
        return format("select * from game where game_id = %d", id);
    }

    @Override
    protected Optional<Game> result(ResultSet resultSet) throws SQLException {
        Game game = null;
        while (resultSet.next()) {
            game = Game.builder()
                    .gameId(resultSet.getInt("game_id"))
                    .gameType(GameType.valueOf(resultSet.getString("game_type")))
                    .build();
        }
        return Optional.ofNullable(game);
    }
}
