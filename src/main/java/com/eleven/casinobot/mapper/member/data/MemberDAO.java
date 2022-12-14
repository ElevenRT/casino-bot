package com.eleven.casinobot.mapper.member.data;

import com.eleven.casinobot.database.annotations.Database;
import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.MemberType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * The member database management class that inherited the database template class.
 * Refer to the member table to forward various queries, generate results with the mapper class
 * @see AbstractDatabaseTemplate
 * @see Database
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
@Database
public class MemberDAO extends AbstractDatabaseTemplate<Member, Long> {

    @Override
    protected String saveQuery(Member entity) {
        String sql = "insert into member(user_id, money, member_type, broken_at, broken)";
        String values = format(
                " values(%s, %s, %s, %s, %s);",
                getDataOrDefault(entity.getUserId()),
                getDataOrDefault(entity.getMoney()),
                getDataOrDefault(entity.getMemberType()),
                getDataOrDefault(entity.getBrokenAt()),
                getDataOrDefault(entity.isBroken())
        );
        return sql + values;
    }

    @Override
    protected String selectByIdQuery(Long id) {
        return format("select * from member where user_id = %d", id);
    }

    @Override
    protected Optional<Member> result(ResultSet resultSet) throws SQLException {
        Member member = null;
        while (resultSet.next()) {
            member = Member.builder()
                    .userId(resultSet.getLong("user_id"))
                    .money(resultSet.getLong("money"))
                    .memberType(MemberType.valueOf(resultSet.getString("member_type")))
                    .brokenAt(getDataOrNull(resultSet.getTimestamp("broken_at"),
                            Timestamp::toLocalDateTime))
                    .broken(resultSet.getBoolean("broken"))
                    .build();
        }
        return Optional.ofNullable(member);
    }
}
