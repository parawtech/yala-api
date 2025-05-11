package tech.rket.shared.infrastructure.apm;

import ch.qos.logback.classic.spi.ILoggingEvent;
import co.elastic.logging.AdditionalField;
import co.elastic.logging.EcsJsonSerializer;
import co.elastic.logging.logback.EcsEncoder;
import org.slf4j.event.KeyValuePair;

import java.util.ArrayList;
import java.util.List;

public class CustomFieldEcsEncoder extends EcsEncoder {
    @Override
    public void setServiceName(String serviceName) {
        super.setServiceName(serviceName);
    }

    @Override
    protected void addCustomFields(ILoggingEvent event, StringBuilder builder) {
        List<KeyValuePair> pairs = event.getKeyValuePairs();
        if (pairs == null) {
            pairs = new ArrayList<>();
        }
        EcsJsonSerializer.serializeAdditionalFields(
                builder,
                pairs.stream().map(e -> new AdditionalField(e.key, (String) e.value)).toList()
        );
    }
}
