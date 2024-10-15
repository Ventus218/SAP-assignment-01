package sap.ass01.solution.backend.layered.persistence.exceptions;

public class ItemNotPersistedException extends Exception {

    public ItemNotPersistedException(String message) {
        super(message);
    }

    public ItemNotPersistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotPersistedException(Throwable cause) {
        super(cause);
    }
}
