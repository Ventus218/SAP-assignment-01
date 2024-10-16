package sap.ass01.solution.backend.layered.businesslogic;

import java.util.*;
import java.util.concurrent.locks.*;
import sap.ass01.solution.backend.layered.businesslogic.exceptions.*;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.*;
import sap.ass01.solution.backend.layered.persistence.CollectionStorage;
import sap.ass01.solution.backend.layered.persistence.exceptions.*;

public class BusinessLogicImpl implements BusinessLogic {

    private final String BIKES = "ebikes";
    private final String USERS = "users";
    private final String RIDES = "rides";
    private final CollectionStorage storage;
    private final Lock transactionLock = new ReentrantLock(true);
    private Condition queue = transactionLock.newCondition();
    private boolean isInTransaction = false;

    public BusinessLogicImpl(CollectionStorage storage) {
        this.storage = storage;
        try {
            storage.createCollection(BIKES);
            storage.createCollection(USERS);
            storage.createCollection(RIDES);
        } catch (IllegalStateException e) {
            // The collection is already persisted
        }
    }

    public void startTransaction() throws InterruptedException {
        synchronized (transactionLock) {
            isInTransaction = true;
        }
    }

    public void endTransaction() {
        synchronized (transactionLock) {
            isInTransaction = false;
            queue.signalAll();
        }
    }

    @Override
    public Collection<EBike> getEBikes() throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.getAllFromCollection(BIKES, EBike.class);
        }
    }

    @Override
    public EBike createEBike(CreateEBikeDTO createEBikeDTO) throws IllegalArgumentException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            var ebike = new EBike(createEBikeDTO.id(), EBikeState.AVAILABLE, createEBikeDTO.loc(),
                    createEBikeDTO.direction(),
                    createEBikeDTO.speed(), createEBikeDTO.batteryLevel());
            try {
                storage.insert(BIKES, createEBikeDTO.id().id(), ebike);
            } catch (DuplicateIdException e) {
                throw new IllegalArgumentException("A bike with id" + createEBikeDTO.id().id() + " already exists", e);
            }
            return ebike;
        }
    }

    @Override
    public Optional<EBike> getEBike(EBikeId ebikeId) throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.find(BIKES, ebikeId.id(), EBike.class);
        }
    }

    @Override
    public void deleteEBike(EBikeId ebikeId) throws NotFoundException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            try {
                storage.delete(BIKES, ebikeId.id());
            } catch (ItemNotPersistedException e) {
                throw new NotFoundException(e);
            }
        }
    }

    @Override
    public Collection<User> getUsers() throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.getAllFromCollection(USERS, User.class);
        }
    }

    @Override
    public User signup(CreateUserDTO createUserDTO) throws IllegalArgumentException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            User user = new User(createUserDTO.id(), 100);
            try {
                storage.insert(USERS, createUserDTO.id().id(), user);
            } catch (DuplicateIdException e) {
                throw new IllegalArgumentException("A user with id" + createUserDTO.id().id() + " already exists", e);
            }
            return user;
        }
    }

    @Override
    public User login(UserId userId) throws NotFoundException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            var user = storage.find(USERS, userId.id(), User.class);
            return user.orElseThrow(() -> new NotFoundException());
        }
    }

    @Override
    public Optional<User> getUser(UserId userId) throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.find(USERS, userId.id(), User.class);
        }
    }

    @Override
    public Collection<Ride> getRides() throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.getAllFromCollection(RIDES, Ride.class);
        }
    }

    @Override
    public Optional<Ride> getRide(RideId rideId) throws InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            return storage.find(RIDES, rideId.id(), Ride.class);
        }
    }

    @Override
    public Ride startRide(StartRideDTO startRideDTO)
            throws NotFoundException, UserAlreadyOnRideException, EBikeAlreadyOnRideException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            if (storage.getAllFromCollection(RIDES, Ride.class).stream()
                    .anyMatch(r -> r.userId().equals(startRideDTO.userId()))) {
                throw new UserAlreadyOnRideException(
                        "The user with id " + startRideDTO.userId().id() + " is already on a ride");
            }
            if (storage.getAllFromCollection(RIDES, Ride.class).stream()
                    .anyMatch(r -> r.ebikeId().equals(startRideDTO.ebikeId()))) {
                throw new EBikeAlreadyOnRideException(
                        "The ebike with id " + startRideDTO.ebikeId().id() + " is already on a ride");
            }
            if (storage.find(USERS, startRideDTO.userId().id(), User.class).isEmpty()) {
                throw new NotFoundException(
                        "No user with id " + startRideDTO.userId().id() + " was found to start a ride");
            }
            if (storage.find(BIKES, startRideDTO.ebikeId().id(), EBike.class).isEmpty()) {
                throw new NotFoundException(
                        "No ebike with id " + startRideDTO.ebikeId().id() + " was found to start a ride");
            }

            var id = new RideId(UUID.randomUUID().toString());
            var ride = new Ride(id, new Date(), Optional.empty(), startRideDTO.userId(), startRideDTO.ebikeId());
            try {
                storage.insert(RIDES, id.id(), ride);
            } catch (DuplicateIdException e) {
                // Should not happen since we are using random generated UUIDs;
                throw new FatalErrorException("A randomly generated UUID for a new ride collided with existing ride",
                        e);
            }
            return ride;
        }
    }

    @Override
    public Ride endRide(EndRideDTO endRideDTO)
            throws NotFoundException, RideAlreadyEndedException, InterruptedException {
        synchronized (transactionLock) {
            while (isInTransaction) {
                queue.await();
            }
            var optional = storage.find(RIDES, endRideDTO.rideId().id(), Ride.class);
            if (optional.isEmpty()) {
                throw new NotFoundException("No ride with id " + endRideDTO.rideId().id() + " was found");
            }
            var ride = optional.get();
            if (ride.endDate().isPresent()) {
                throw new RideAlreadyEndedException("The ride is already ended");
            }
            ride = new Ride(ride.id(), ride.startedDate(), Optional.of(new Date()), ride.userId(), ride.ebikeId());
            try {
                storage.update(RIDES, ride.id().id(), ride);
            } catch (ItemNotPersistedException e) {
                // Should not happen as the ride was retreived
                throw new FatalErrorException("Unexpected update of ride failed", e);
            }
            return ride;
        }
    }

}
