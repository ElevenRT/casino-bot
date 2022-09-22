package context;

import com.eleven.casinobot.config.DatabaseConfig;
import com.eleven.casinobot.mapper.member.Member;
import com.eleven.casinobot.mapper.member.data.MemberDAO;
import com.eleven.casinobot.core.context.ComponentContext;
import com.eleven.casinobot.event.test.TestListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTest {

    @Test
    public void autoInjectionTest() throws Exception {
        // when
        ComponentContext componentContext = new ComponentContext(true);
        @SuppressWarnings("deprecation")
        TestListener testListener = componentContext.getInstance(TestListener.class);

        // then
        assertThat(testListener).isNotNull();
        assertThat(testListener.getMemberDAO()).isNotNull();
    }

    @Test
    public void contextToUseQueryTest() throws Exception {
        // given
        DatabaseConfig.initalizeDatabase();
        ComponentContext componentContext = new ComponentContext(true);
        Member member = Member.builder()
                .userId(1L)
                .build();

        // when
        @SuppressWarnings("deprecation")
        TestListener testListener = componentContext.getInstance(TestListener.class);
        MemberDAO memberDAO = (MemberDAO) testListener.getMemberDAO();
        memberDAO.save(member);
    }
}