package tech.rket.auth.application.user.exception;

public class UserIsAlreadyExistException extends RuntimeException {
    public UserIsAlreadyExistException(String email) {
    }
}
