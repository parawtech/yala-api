package tech.rket.shared.infrastructure.phonelib;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/phonelib")
public class PhonelibController {
    @GetMapping
    public ResponseEntity<Map<String, Map<String, Object>>> getRegex(HttpServletResponse response) {
        response.setHeader("Cache-Control", "public,max-age=640800");
        Map<String, Map<String, Object>> map = PhoneNumberHelper.getMobileRegexPerRegion();
        return ResponseEntity.ok(map);
    }

    @GetMapping("{countryCode}")
    public ResponseEntity<Map<String, Object>> getRegex(@PathVariable("countryCode") String countryCode, HttpServletResponse response) {
        response.setHeader("Cache-Control", "public,max-age=640800");
        Map<String, Object> value = PhoneNumberHelper.getMobileRegexForRegion(countryCode.toUpperCase());
        return !value.isEmpty() ? ResponseEntity.ok(Map.of("pattern", value)) : ResponseEntity.notFound().build();
    }

    @GetMapping("{countryCode}/{mobileNumber}")
    public ResponseEntity<Map<String, String>> normalize(@PathVariable("countryCode") String countryCode, @PathVariable("mobileNumber") String mobileNumber, HttpServletResponse response) {
        String value = PhoneNumberHelper.normalize(mobileNumber, countryCode.toUpperCase()).orElse(null);
        return value != null ? ResponseEntity.ok(Map.of("value", value)) : ResponseEntity.notFound().build();
    }
}
