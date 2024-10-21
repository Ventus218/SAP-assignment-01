package sap.ass01.solution.backend.hexagonal.ports.persistence.exceptions;

public class DuplicateIdException extends Exception {

    public DuplicateIdException(String message) {
        super(message);
    }

    public DuplicateIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateIdException(Throwable cause) {
        super(cause);
    }
}
