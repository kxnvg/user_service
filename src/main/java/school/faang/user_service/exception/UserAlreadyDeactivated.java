package school.faang.user_service.exception;

public class UserAlreadyDeactivated extends RuntimeException{
    public UserAlreadyDeactivated(String message) {
        super(message);
    }
}
