package sap.ass01.solution.frontend.model;

import java.util.*;
import java.util.function.*;
import sap.ass01.solution.frontend.model.dto.*;
import sap.ass01.solution.frontend.utils.Result;

public class HTTPAPIsMock implements HTTPAPIs {

    private final Map<EBikeId, EBike> bikes = new HashMap<>();
    private final Map<UserId, User> users = new HashMap<>();
    private final Map<RideId, Ride> rides = new HashMap<>();
    private final long delayMillis;

    public HTTPAPIsMock(long delayMillis) {
        this.delayMillis = delayMillis;
        var u1 = new User(new UserId("u1"), 100);
        users.put(u1.id(), u1);
        var b1 = new EBike(new EBikeId("b1"), EBikeState.AVAILABLE, new P2d(0, 0), new V2d(1, 0), 0, 100);
        bikes.put(b1.id(), b1);
    }

    @Override
    public void getEBikes(Consumer<Result<Iterable<EBike>, Throwable>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(bikes.values()));
        });
    }

    @Override
    public void createEBike(CreateEBikeDTO dto, Consumer<Result<EBike, Throwable>> handler) {
        runDelayedThread(() -> {
            bikes.put(dto.id(), new EBike(dto.id(), EBikeState.AVAILABLE, dto.loc(), dto.direction(), dto.speed(),
                    dto.batteryLevel()));
            handler.accept(Result.success(null));
            // handler.accept(Result.failure(new Exception("sosoooss")));
        });
    }

    @Override
    public void deleteEBike(EBikeId id, Consumer<Result<Void, Throwable>> handler) {
        runDelayedThread(() -> {
            bikes.remove(id);
            handler.accept(Result.success(null));
        });
    }

    @Override
    public void getUsers(Consumer<Result<Iterable<User>, Throwable>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(users.values()));
        });
    }

    @Override
    public void signup(CreateUserDTO dto, Consumer<Result<User, Throwable>> handler) {
        runDelayedThread(() -> {
            users.put(dto.id(), new User(dto.id(), 100));
            handler.accept(Result.success(null));
        });
    }

    @Override
    public void login(UserId id, Consumer<Result<User, Throwable>> handler) {
        runDelayedThread(() -> {
            if (users.containsKey(id)) {
                handler.accept(Result.success(null));
            } else {
                handler.accept(Result.failure(new IllegalArgumentException("User does not exists")));
            }
        });
    }

    @Override
    public void getRides(Consumer<Result<Iterable<Ride>, Throwable>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(rides.values()));
        });
    }

    @Override
    public void startRide(StartRideDTO dto, Consumer<Result<Ride, Throwable>> handler) {
        runDelayedThread(() -> {
            var rideId = new RideId(UUID.randomUUID().toString());
            var ride = new Ride(rideId, new Date(), Optional.empty(), dto.userId(), dto.ebikeId());
            rides.put(ride.id(), ride);
            handler.accept(Result.success(ride));
        });
    }

    @Override
    public void endRide(RideId id, Consumer<Result<Ride, Throwable>> handler) {
        runDelayedThread(() -> {
            var oldRide = rides.get(id);
            var newRide = new Ride(oldRide.id(), oldRide.startedDate(), Optional.of(new Date()), oldRide.userId(),
                    oldRide.ebikeId());
            rides.put(id, newRide);
            handler.accept(Result.success(newRide));
        });
    }

    private void runDelayedThread(Runnable runnable) {
        runDelayedThread(delayMillis, runnable);
    }

    private void runDelayedThread(long delayMillis, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
            }
            runnable.run();
        }).start();
    }
}
