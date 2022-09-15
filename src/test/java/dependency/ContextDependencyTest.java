package dependency;

import com.eleven.casinobot.context.event.EventContext;
import com.eleven.casinobot.event.test.TestListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextDependencyTest {

    @Test
    public void constructorInjectionTest() throws Exception {
        // given
        EventContext eventContext = new EventContext(true);

        // when
        @SuppressWarnings("deprecation")
        TestListener testListener = eventContext.getInstance(TestListener.class);

        // then
        assertThat(testListener).isNotNull();
        assertThat(testListener.getMemberDAO()).isNotNull();
        assertThat(testListener.getAbc()).isNotNull();
    }
}
