package tech.rket.auth.domain.core.service;

public interface UserPasswordEncoder {
    boolean matches(String rawPassword, String encodedPassword);

    String encodePassword(String rawPassword);
}
