package sap.ass01.solution.frontend.model;

import java.util.*;
import java.util.function.*;
import sap.ass01.solution.frontend.utils.Result;

public class HTTPAPIsMock implements HTTPAPIs {

    private final Map<EBikeId, EBike> bikes = new HashMap<>();
    private final Map<UserId, User> users = new HashMap<>();
    private final Map<RideId, Ride> rides = new HashMap<>();

    public HTTPAPIsMock() {
        var u1 = new User(new UserId("u1"), 100);
        users.put(u1.id(), u1);
        var b1 = new EBike(new EBikeId("b1"), EBikeState.AVAILABLE, new P2d(0, 0), new V2d(1, 0), 0, 100);
        bikes.put(b1.id(), b1);
    }

    @Override
    public void getEBikes(Consumer<Result<Iterable<EBike>, Exception>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(bikes.values()));
        });
    }

    @Override
    public void createEBike(EBike bike, Consumer<Result<Void, Exception>> handler) {
        runDelayedThread(() -> {
            bikes.put(bike.id(), bike);
            handler.accept(Result.success(null));
            // handler.accept(Result.failure(new Exception("sosoooss")));
        });
    }

    @Override
    public void deleteEBike(EBikeId id, Consumer<Result<Void, Exception>> handler) {
        runDelayedThread(() -> {
            bikes.remove(id);
            handler.accept(Result.success(null));
        });
    }

    @Override
    public void getUsers(Consumer<Result<Iterable<User>, Exception>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(users.values()));
        });
    }

    @Override
    public void createUser(User user, Consumer<Result<Void, Exception>> handler) {
        runDelayedThread(() -> {
            users.put(user.id(), user);
            handler.accept(Result.success(null));
        });
    }

    @Override
    public void deleteUser(UserId id, Consumer<Result<Void, Exception>> handler) {
        runDelayedThread(() -> {
            users.remove(id);
            handler.accept(Result.success(null));
        });
    }

    @Override
    public void getRides(Consumer<Result<Iterable<Ride>, Exception>> handler) {
        runDelayedThread(() -> {
            handler.accept(Result.success(rides.values()));
        });
    }

    @Override
    public void startRide(UserId userId, EBikeId eBikeId, Consumer<Result<Ride, Exception>> handler) {
        runDelayedThread(() -> {
            var rideId = new RideId(UUID.randomUUID().toString());
            var ride = new Ride(rideId, new Date(), Optional.empty(), userId, eBikeId);
            rides.put(ride.id(), ride);
            handler.accept(Result.success(ride));
        });
    }

    @Override
    public void endRide(RideId id, Consumer<Result<Ride, Exception>> handler) {
        runDelayedThread(() -> {
            var oldRide = rides.get(id);
            var newRide = new Ride(oldRide.id(), oldRide.startedDate(), Optional.of(new Date()), oldRide.userId(),
                    oldRide.ebikeId());
            rides.put(id, newRide);
            handler.accept(Result.success(newRide));
        });
    }

    private void runDelayedThread(Runnable runnable) {
        runDelayedThread(750, runnable);
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
