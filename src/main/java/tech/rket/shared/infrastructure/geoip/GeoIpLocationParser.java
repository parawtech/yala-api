package tech.rket.shared.infrastructure.geoip;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.zip.GZIPInputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeoIpLocationParser {
    private static DatabaseReader dbReader;

    private static DatabaseReader dbReader() throws IOException {
        if (dbReader == null) {
            File database = new ClassPathResource("static/archive/GeoLite2-City.mmdb.gz").getFile();

            try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(database))) {
                dbReader = new DatabaseReader.Builder(gis).build();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    dbReader.close();
                    dbReader = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        return dbReader;
    }

    public static GeoIpLocation parse(String ip) {
        if (ip == null || ip.isBlank()) {
            return new GeoIpLocation(ip, null, null, null, null, null);
        }
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader().city(ipAddress);

            return new GeoIpLocation(
                    ip,
                    response.getContinent().getName(),
                    response.getCountry().getIsoCode(),
                    response.getSubdivisions().isEmpty() ? null : response.getSubdivisions().get(0).getName(),
                    response.getCity().getName(),
                    response.getLocation().getTimeZone()
            );

        } catch (IOException | GeoIp2Exception e) {
            return new GeoIpLocation(ip, null, null, null, null, null);
        }
    }
}
