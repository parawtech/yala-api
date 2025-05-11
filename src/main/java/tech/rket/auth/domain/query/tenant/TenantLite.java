package tech.rket.auth.domain.query.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tech.rket.shared.core.query.QueryObject;

@Getter
@EqualsAndHashCode
@ToString
public final class TenantLite implements QueryObject<Long> {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final Long id;
    private final String name;

    public TenantLite(Long id, Object name) {
        this.id = id;
        this.name = (String) name;
    }
}
