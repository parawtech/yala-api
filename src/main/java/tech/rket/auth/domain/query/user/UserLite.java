package tech.rket.auth.domain.query.user;

import lombok.Getter;
import tech.rket.shared.core.query.QueryObject;

@Getter
public class UserLite implements QueryObject<Long> {
    private final Long id;
    private final String email;
    private final String name;
    private final String mobile;

    public UserLite(Object id, Object mobile, Object email, Object name) {
        this.id = (Long) id;
        this.mobile = (String) mobile;
        this.email = (String) email;
        this.name = (String) name;
    }
}