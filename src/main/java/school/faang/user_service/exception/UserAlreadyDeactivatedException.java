package school.faang.user_service.exception;

public class UserAlreadyDeactivatedException extends RuntimeException{
    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}
