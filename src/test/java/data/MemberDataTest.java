package data;

import com.eleven.casinobot.config.DatabaseConfig;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.MemberType;
import com.eleven.casinobot.mapper.member.data.MemberDAO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

public class MemberDataTest {
    private static final Logger log = LoggerFactory.getLogger(MemberDataTest.class);

    private final MemberDAO memberDAO = new MemberDAO();

    @Test
    public void saveAndSelectTest() {
        // given
        DatabaseConfig.initalizeDatabase();

        Instant saveInstant = Instant.now();
        Member member = Member.builder()
                .build();
        memberDAO.save(member);
        Duration saveDuration = Duration.between(saveInstant, Instant.now());
        log.info("save duration : {}ms", saveDuration.toMillis());

        // when
        Instant findInstant = Instant.now();
        Member savedMember = memberDAO.selectById(1L);
        Duration findDuration = Duration.between(findInstant, Instant.now());
        log.info("find duration: {}ms", findDuration.toMillis());

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getMemberType()).isEqualTo(MemberType.GENERAL);
        assertThat(savedMember.isBroken()).isFalse();
    }
}
