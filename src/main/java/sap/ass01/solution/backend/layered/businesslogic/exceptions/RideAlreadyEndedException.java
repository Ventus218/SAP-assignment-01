package sap.ass01.solution.backend.layered.businesslogic.exceptions;

public class RideAlreadyEndedException extends Exception {

    public RideAlreadyEndedException(String message) {
        super(message);
    }

    public RideAlreadyEndedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RideAlreadyEndedException(Throwable cause) {
        super(cause);
    }

    public RideAlreadyEndedException() {
        super();
    }
}
