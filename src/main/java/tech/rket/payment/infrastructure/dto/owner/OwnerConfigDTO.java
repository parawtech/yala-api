package tech.rket.payment.infrastructure.dto.owner;

import tech.rket.payment.domain.shared.IPG;
import lombok.Data;

import java.util.Map;

@Data
public class OwnerConfigDTO {
    private IPG ipg;
    private Map<String, Object> value;
}