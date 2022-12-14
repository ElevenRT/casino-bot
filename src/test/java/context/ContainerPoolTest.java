package context;

import com.eleven.casinobot.config.DatabaseConfig;
import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import com.eleven.casinobot.database.container.ContainerPool;
import com.eleven.casinobot.mapper.member.Member;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("rawtypes")
public class ContainerPoolTest {

    @Test
    public void findTemplateAndSaveTest() throws CloneNotSupportedException {
        // given
        Map<String, AbstractDatabaseTemplate> databaseTemplateSet = ContainerPool.getInstance()
                .getDatabaseTemplateContainer();

        // when
        assertThat(databaseTemplateSet).isNotEmpty();
        assertThat(databaseTemplateSet.size()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void proxySaveTest() throws CloneNotSupportedException {
        // given
        DatabaseConfig.initalizeDatabase();
        Map<String, AbstractDatabaseTemplate> databaseTemplates = ContainerPool.getInstance()
                .getDatabaseTemplateContainer();
        Member member = Member.builder().build();

        // when
        for (Map.Entry<String, AbstractDatabaseTemplate> templateEntry
                : databaseTemplates.entrySet()) {
            String proxyName = templateEntry.getKey();
            if (proxyName.equals("MemberDAO")) {
                templateEntry.getValue().save(member);
            }
        }
    }
}
