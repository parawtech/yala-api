package tech.rket.payment.infrastructure.dto.owner;

import tech.rket.payment.domain.transaction.OwnerSelfConfig;
import lombok.Data;

import java.util.List;

@Data
public class OwnerDTO {
    private String name;
    private String description;
    private String identifier;
    private List<OwnerConfigDTO> configs;
    private OwnerSelfConfig selfConfig;
}
