package tech.rket.shared.infrastructure.apm;

import co.elastic.apm.attach.ElasticApmAttacher;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Setter
@Configuration
@ConfigurationProperties(prefix = "elastic.apm")
@ConditionalOnProperty(value = "elastic.apm.enabled", havingValue = "true")
public class ElasticApmConfig {
    private String serverUrl;
    private String serviceName;
    private String applicationPackages;
    private String secretToken;
    private String environment;
    private String logLevel;
    private boolean usePathAsTransactionName = true;

    @PostConstruct
    public void init() {
        Map<String, String> apmProps = new HashMap<>(7);
        apmProps.put("server_url", serverUrl);
        apmProps.put("service_name", serviceName);
        apmProps.put("application_packages", applicationPackages);
        if (secretToken != null) {
            apmProps.put("secret_token", secretToken);
        }
        if (environment != null) {
            apmProps.put("environment", environment);
        }
        if (logLevel != null) {
            apmProps.put("log_level", logLevel);
        }
        apmProps.put("use_path_as_transaction_name", usePathAsTransactionName ? "true" : "false");

        ElasticApmAttacher.attach(apmProps);
    }
}
