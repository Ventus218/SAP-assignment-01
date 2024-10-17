package sap.ass01.solution.frontend.model;

import java.util.function.Consumer;
import sap.ass01.solution.frontend.utils.Result;

public interface HTTPAPIs {

    /* EBikes */

    void getEBikes(Consumer<Result<Iterable<EBike>, Exception>> handler);

    void createEBike(EBike bike, Consumer<Result<Void, Exception>> handler);

    void deleteEBike(EBikeId id, Consumer<Result<Void, Exception>> handler);

    /* Users */

    void getUsers(Consumer<Result<Iterable<User>, Exception>> handler);

    void createUser(User user, Consumer<Result<Void, Exception>> handler);

    void deleteUser(UserId id, Consumer<Result<Void, Exception>> handler);

    /* Rides */

    void getRides(Consumer<Result<Iterable<Ride>, Exception>> handler);

    void startRide(UserId userId, EBikeId eBikeId, Consumer<Result<Ride, Exception>> handler);

    void endRide(RideId id, Consumer<Result<Ride, Exception>> handler);
}
