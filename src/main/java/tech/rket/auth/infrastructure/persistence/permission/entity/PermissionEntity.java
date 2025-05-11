package tech.rket.auth.infrastructure.persistence.permission.entity;

import tech.rket.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

@Entity
@Table(name = "auth_permission")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class PermissionEntity extends BaseEntity implements PersistedObject<String> {
    @Id
    private String id;
    private String name;
    private String description;

    public static PermissionEntity build(String id, String name, String description) {
        PermissionEntity entity = new PermissionEntity();
        entity.id = id;
        entity.name = name;
        entity.description = description;
        return entity;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
