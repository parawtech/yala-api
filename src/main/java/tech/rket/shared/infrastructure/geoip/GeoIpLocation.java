package tech.rket.shared.infrastructure.geoip;

public record GeoIpLocation(
        String ipAddress,
        String continent,
        String country,
        String region,
        String city,
        String timeZone
) {
}
