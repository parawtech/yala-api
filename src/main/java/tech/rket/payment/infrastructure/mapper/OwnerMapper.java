package tech.rket.payment.infrastructure.mapper;

import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.owner.OwnerConfig;
import tech.rket.payment.infrastructure.dto.owner.OwnerConfigDTO;
import tech.rket.payment.infrastructure.dto.owner.OwnerDTO;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface OwnerMapper {
    Owner convert(OwnerDTO dto);

    OwnerDTO convert(Owner entity);

    OwnerConfig convert(OwnerConfigDTO dto);

    OwnerConfigDTO convert(OwnerConfig entity);
}
