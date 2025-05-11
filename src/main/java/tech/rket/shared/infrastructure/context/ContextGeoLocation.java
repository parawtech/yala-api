package tech.rket.shared.infrastructure.context;

public record ContextGeoLocation(
        String continent,
        String country,
        String region,
        String city) {
}
