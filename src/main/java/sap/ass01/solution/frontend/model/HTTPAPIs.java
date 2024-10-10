package sap.ass01.solution.frontend.model;

import java.util.function.Consumer;
import sap.ass01.solution.frontend.utils.Result;

public interface HTTPAPIs {

    /* EBikes */

    void getEBikes(Consumer<Result<Iterable<EBike>, Exception>> handler);

    void createEBike(EBike bike, Consumer<Result<Void, Exception>> handler);

    void deleteEBike(String id, Consumer<Result<Void, Exception>> handler);

    /* Users */

    void getUsers(Consumer<Result<Iterable<EBike>, Exception>> handler);

    void createUser(User user, Consumer<Result<Void, Exception>> handler);

    void deleteUser(String id, Consumer<Result<Void, Exception>> handler);

    /* Rides */

    void getRides(Consumer<Result<Iterable<EBike>, Exception>> handler);

    void startRide(String userId, String eBikeId, Consumer<Result<Ride, Exception>> handler);

    void endRide(String id, Consumer<Result<Ride, Exception>> handler);
}
