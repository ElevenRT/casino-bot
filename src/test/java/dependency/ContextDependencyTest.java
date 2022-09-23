package dependency;

import com.eleven.casinobot.core.context.ComponentContext;
import com.eleven.casinobot.event.test.TestListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextDependencyTest {

    @Test
    public void constructorInjectionTest() throws Exception {
        // given
        ComponentContext componentContext = new ComponentContext(true);

        // when
        @SuppressWarnings("deprecation")
        TestListener testListener = componentContext.getInstance(TestListener.class);

        // then
        assertThat(testListener).isNotNull();
        assertThat(testListener.getMemberDAO()).isNotNull();
        assertThat(testListener.getAbc()).isNotNull();
    }
}
