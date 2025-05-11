package tech.rket.payment.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;

@AllArgsConstructor
@Getter
public class Redirect {
    private final String url;
    private final LinkedHashMap<String, Object> form = new LinkedHashMap<>();
    private final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

    public Redirect putForm(String key, Object value) {
        form.put(key, value);
        return this;
    }

    public Redirect putData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public boolean isForm() {
        return form.size() > 0;
    }
}
