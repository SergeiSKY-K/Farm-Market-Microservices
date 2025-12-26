package telran.java57.authservice.dto.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("user with id " + id + "not found");
    }
}
