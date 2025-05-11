package tech.rket.shared.infrastructure.mapstruct;

import org.mapstruct.*;

@MapperConfig(
        componentModel = "spring",
        uses = {MapperHelper.class},
        injectionStrategy = InjectionStrategy.FIELD,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unexpectedValueMappingException = MappingException.class
)
public interface MapstructConfig {
}
