package tech.rket.auth.domain.core.user.commands;

import tech.rket.auth.domain.core.user.UserProfile;
import tech.rket.shared.core.domain.command.DomainCommand;

public record UserProfileUpdate(UserProfile profile,String locale) implements DomainCommand {
}
