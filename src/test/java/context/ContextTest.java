package context;

import com.eleven.casinobot.config.DatabaseConfig;
import com.eleven.casinobot.entity.member.Member;
import com.eleven.casinobot.entity.member.data.MemberDAO;
import com.eleven.casinobot.event.context.EventContext;
import com.eleven.casinobot.event.test.TestListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTest {

    @Test
    public void autoInjectionTest() throws Exception {
        // when
        EventContext eventContext = new EventContext(true);
        @SuppressWarnings("deprecation")
        TestListener testListener = eventContext.getInstance(TestListener.class);

        // then
        assertThat(testListener).isNotNull();
        assertThat(testListener.getMemberDAO()).isNotNull();
    }

    @Test
    public void contextToUseQueryTest() throws Exception {
        // given
        DatabaseConfig.initalizeDatabase();
        EventContext eventContext = new EventContext(true);
        Member member = new Member((Long) null);

        // when
        @SuppressWarnings("deprecation")
        TestListener testListener = eventContext.getInstance(TestListener.class);
        MemberDAO memberDAO = (MemberDAO) testListener.getMemberDAO();
        memberDAO.save(member);
    }


}
