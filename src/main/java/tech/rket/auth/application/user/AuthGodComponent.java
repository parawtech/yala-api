package tech.rket.auth.application.user;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Component;
import tech.rket.auth.domain.core.service.UserPasswordEncoder;
import tech.rket.auth.domain.core.user.PersonProfile;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserRepository;
import tech.rket.auth.domain.core.user.commands.DefaultMembershipAdd;
import tech.rket.auth.domain.core.user.commands.UserCreate;
import tech.rket.auth.domain.core.user.commands.UserProfileUpdate;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGodComponent {
    private final UserRepository userRepository;
    private final UserPasswordEncoder userPasswordEncoder;
    @Value("${oauth.god.username}")
    @Getter
    private String username;
    @Value("${oauth.god.password}")
    private String password;
    private User god;
    private final DefaultTenantFinder finder;

    @PostConstruct
    public void createGod() {
        try {
            if (!userRepository.existsByEmail(username)) {
                User user = User.create(
                                new UserCreate(
                                        userRepository.generateID(),
                                        Locale.getDefault().toLanguageTag(),
                                        username,
                                        null)
                        )
                        .throwIfFailure(DomainConstraintViolationException::new)
                        .value();

                user.updateProfile(new UserProfileUpdate(new PersonProfile(null, "GOD", null, null, null), null));

                password = Sha512DigestUtils.shaHex(password);
                user.setPassword(userPasswordEncoder, password);
                AppointedTenant appointed = finder.appoint(user.getEmail(), null);
                if (!appointed.tenant().isWorkTenant()) {
                    throw new IllegalArgumentException("God should use non free email !.");
                }
                user.addDefaultMembership(new DefaultMembershipAdd(appointed.tenant(), appointed.role()));
                userRepository.save(user);
                god = user;
            }
            password = null;
        } catch (Exception ignored) {
            log.error("Exception in god component:{}", ignored.getMessage(), ignored);
        }
    }

    public User get() {
        if (god == null) {
            god = userRepository.findByEmail(username).orElseThrow();
        }
        return god;
    }
}
