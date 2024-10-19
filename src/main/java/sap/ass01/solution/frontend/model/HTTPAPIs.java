package sap.ass01.solution.frontend.model;

import java.util.function.Consumer;
import sap.ass01.solution.frontend.model.dto.*;
import sap.ass01.solution.frontend.utils.Result;

public interface HTTPAPIs {

    /* EBikes */

    void getEBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler);

    void createEBike(CreateEBikeDTO dto, Consumer<Result<EBike, Throwable>> handler);

    void deleteEBike(EBikeId id, Consumer<Result<Void, Throwable>> handler);

    /* Users */

    void getUsers(Consumer<Result<Iterable<User>, Throwable>> handler);

    void signup(CreateUserDTO dto, Consumer<Result<User, Throwable>> handler);

    void login(UserId id, Consumer<Result<User, Throwable>> handler);

    /* Rides */

    void getRides(Consumer<Result<Iterable<Ride>, Throwable>> handler);

    void startRide(StartRideDTO dto, Consumer<Result<Ride, Throwable>> handler);

    void endRide(RideId id, Consumer<Result<Ride, Throwable>> handler);
}
