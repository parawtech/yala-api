package tech.rket.shared.infrastructure.mapstruct;

import org.mapstruct.*;

public interface AppendMapper<A, B> {
    @InheritConfiguration
    @BeanMapping(
            nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    A append(@MappingTarget A target, B source);
}
