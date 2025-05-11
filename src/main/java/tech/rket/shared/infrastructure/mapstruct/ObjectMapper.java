package tech.rket.shared.infrastructure.mapstruct;

public interface ObjectMapper<A, B> {
    A map(B source);
}
