package tech.rket.shared.infrastructure.scheduling;

import lombok.Data;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "shared.scheduling.jdbc")
@ConditionalOnProperty(value = "shared.scheduling.shedlock.lock-type", havingValue = "jdbc")
@ConditionalOnBean(ShedLockConfig.class)
@Data
public class ShedLockJdbcLockProviderConfig {
    private boolean dbUpperCase = false;
    private String tableName = "shedlock";
    private boolean useDbTime = false;
    private TimeZone timeZone;
    private Integer isolationLevel;
    private boolean throwUnexpectedException = false;
    private JdbcTemplateLockProvider.ColumnNames columnNames;

    @Bean
    public JdbcTemplateLockProvider jdbcTemplateLockProvider(DataSource dataSource, PlatformTransactionManager transactionManager) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        JdbcTemplateLockProvider.Configuration.Builder builder = JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(jdbcTemplate)
                .withTableName(this.getTableName())
                .withLockedByValue("shared-scheduler")
                .withThrowUnexpectedException(this.isThrowUnexpectedException());

        if (this.getTimeZone() != null) {
            builder.withTimeZone(this.getTimeZone());
        }

        if (this.isUseDbTime()) {
            builder.usingDbTime();
        }

        if (this.getIsolationLevel() != null) {
            builder.withIsolationLevel(this.getIsolationLevel());
        }

        if (this.isDbUpperCase()) {
            builder.withDbUpperCase(true);
        }

        if (transactionManager != null) {
            builder.withTransactionManager(transactionManager);
        }

        return new JdbcTemplateLockProvider(builder.build());
    }


}
