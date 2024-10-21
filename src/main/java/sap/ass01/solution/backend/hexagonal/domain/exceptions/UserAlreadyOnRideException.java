package sap.ass01.solution.backend.hexagonal.domain.exceptions;

public class UserAlreadyOnRideException extends Exception {

    public UserAlreadyOnRideException(String message) {
        super(message);
    }

    public UserAlreadyOnRideException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyOnRideException(Throwable cause) {
        super(cause);
    }

    public UserAlreadyOnRideException() {
        super();
    }
}
