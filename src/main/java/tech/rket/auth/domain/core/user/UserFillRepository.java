package tech.rket.auth.domain.core.user;

public interface UserFillRepository {
    void fillJoinInvitations(User user);

    void fillRegisterInvitations(User user);
}
