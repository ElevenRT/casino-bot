package context;

import com.eleven.casinobot.event.context.EventContext;
import com.eleven.casinobot.event.test.TestListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTest {

    @Test
    public void autoInjectionTest() throws Exception {
        // when
        EventContext eventContext = new EventContext();
        @SuppressWarnings("deprecation")
        TestListener testListener = eventContext.getInstance(TestListener.class);

        // then
        assertThat(testListener).isNotNull();
        assertThat(testListener.getMemberDAO()).isNotNull();
    }

}
