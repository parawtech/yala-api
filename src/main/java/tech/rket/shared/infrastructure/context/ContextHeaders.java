package tech.rket.shared.infrastructure.context;

public record ContextHeaders(
        String locale,
        String timezone,
        String continent,
        String country,
        String region,
        String city
) {
}
