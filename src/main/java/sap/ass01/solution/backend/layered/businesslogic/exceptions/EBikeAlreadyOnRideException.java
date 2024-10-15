package sap.ass01.solution.backend.layered.businesslogic.exceptions;

public class EBikeAlreadyOnRideException extends Exception {

    public EBikeAlreadyOnRideException(String message) {
        super(message);
    }

    public EBikeAlreadyOnRideException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBikeAlreadyOnRideException(Throwable cause) {
        super(cause);
    }

    public EBikeAlreadyOnRideException() {
        super();
    }
}
