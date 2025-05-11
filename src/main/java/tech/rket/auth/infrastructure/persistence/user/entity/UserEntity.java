package tech.rket.auth.infrastructure.persistence.user.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "auth_user")
@NoArgsConstructor
@Getter
public class UserEntity extends BaseEntity implements PersistedObject<Long> {
    @Id
    private Long id;
    private String email;
    private String password;
    private String mobile;
    private String name;
    private String locale;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<MembershipEntity> memberships = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<InvitationEntity> invitations;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> profile;

    public static UserEntity build(Long id, String email, String mobile, String password, String locale) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = id;
        userEntity.email = email;
        userEntity.mobile = mobile;
        userEntity.password = password;
        userEntity.locale = locale;
        userEntity.memberships = new HashSet<>();
        userEntity.invitations = new HashSet<>();
        return userEntity;
    }

    public void update(String password, Map<String, Object> profile, String locale) {
        this.profile = profile;
        this.password = password;
        this.locale = locale;
    }
}
