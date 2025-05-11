package tech.rket.auth.application.user.command;


import jakarta.annotation.Nullable;
import tech.rket.auth.domain.core.user.UserProfile;

public record UserUpdateProfileCommand(UserProfile profile, @Nullable String locale) {
}
