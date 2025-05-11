package tech.rket.payment.domain.owner;

import tech.rket.payment.domain.shared.IPG;
import lombok.Data;

import java.util.Map;

@Data
public class OwnerConfig {
    private IPG ipg;
    private Map<String, Object> value;
}