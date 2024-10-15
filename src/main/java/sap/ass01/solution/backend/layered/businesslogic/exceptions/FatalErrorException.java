package sap.ass01.solution.backend.layered.businesslogic.exceptions;

public class FatalErrorException extends RuntimeException {
    public FatalErrorException() {
        super();
    }

    public FatalErrorException(String message) {
        super(message);
    }

    public FatalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalErrorException(Throwable cause) {
        super(cause);
    }
}
