package proxy;

import com.eleven.casinobot.context.proxy.ProxyFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyFactoryTest {

    public interface IStringTest {
        String getResult();
    }

    public static class StringTest implements IStringTest {
        @Override
        public String getResult() {
            return "Player1";
        }
    }

    @Test
    public void proxyTest() {
        // when
        IStringTest stringTest = ProxyFactory.newProxy(StringTest.class, IStringTest.class);

        // then
        assertThat(stringTest).isNotNull();
        assertThat(stringTest.getResult()).isEqualTo("Player1");
    }

    @Test
    public void proxyBeforeTest() {
        // when
        IStringTest stringTest = ProxyFactory.newProxy(
                StringTest.class, IStringTest.class,
                System.out::println
        );

        // then
        assertThat(stringTest).isNotNull();
        assertThat(stringTest.getResult()).isEqualTo("Player1");
    }

    @Test
    public void proxyAfterTest() {
        // when
        IStringTest stringTest = ProxyFactory.newProxy(
                StringTest.class, IStringTest.class,
                iStringTest -> System.out.println("before"),
                iStringTest -> System.out.println("after")
        );

        // then
        assertThat(stringTest).isNotNull();
        assertThat(stringTest.getResult()).isEqualTo("Player1");
    }
}
