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

    public void startTransaction() {
        transactionLock.lock();
    }

    public void endTransaction() {
        transactionLock.unlock();
    }

    @Override
    public Collection<EBike> getEBikes() {
        try {
            transactionLock.lock();
            return storage.getAllFromCollection(BIKES, EBike.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public EBike createEBike(CreateEBikeDTO createEBikeDTO) throws IllegalArgumentException {
        try {
            transactionLock.lock();
            if (createEBikeDTO.direction().abs() == 0) {
                throw new IllegalArgumentException("EBike cannot have a null direction");
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
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Optional<EBike> getEBike(EBikeId ebikeId) {
        try {
            transactionLock.lock();
            return storage.find(BIKES, ebikeId.id(), EBike.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public void deleteEBike(EBikeId ebikeId) throws NotFoundException {
        try {
            transactionLock.lock();
            try {
                storage.delete(BIKES, ebikeId.id());
            } catch (ItemNotPersistedException e) {
                throw new NotFoundException(e);
            }
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public EBike updateEBike(EBikeId ebikeId, UpdateEBikeDTO updateEBikeDTO)
            throws NotFoundException {
        try {
            transactionLock.lock();
            EBike bike = getEBike(ebikeId).orElseThrow(() -> new NotFoundException());
            EBike newBike = new EBike(ebikeId, bike.state(), updateEBikeDTO.loc(), updateEBikeDTO.direction(),
                    updateEBikeDTO.speed(), updateEBikeDTO.batteryLevel());
            try {
                storage.update(BIKES, ebikeId.id(), newBike);
            } catch (ItemNotPersistedException e) {
                throw new FatalErrorException("Unexpected update of bike failed", e);
            }
            return newBike;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Collection<User> getUsers() {
        try {
            transactionLock.lock();
            return storage.getAllFromCollection(USERS, User.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public User signup(CreateUserDTO createUserDTO) throws IllegalArgumentException {
        try {
            transactionLock.lock();
            User user = new User(createUserDTO.id(), 100);
            try {
                storage.insert(USERS, createUserDTO.id().id(), user);
            } catch (DuplicateIdException e) {
                throw new IllegalArgumentException("A user with id" + createUserDTO.id().id() + " already exists", e);
            }
            return user;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public User login(UserId userId) throws NotFoundException {
        try {
            transactionLock.lock();
            var user = storage.find(USERS, userId.id(), User.class);
            return user.orElseThrow(() -> new NotFoundException());
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        try {
            transactionLock.lock();
            return storage.find(USERS, userId.id(), User.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public User updateUser(UserId id, UpdateUserDTO updateUserDTO) throws NotFoundException {
        try {
            transactionLock.lock();
            // Ensuring user exists
            getUser(id).orElseThrow(() -> new NotFoundException());
            User newUser = new User(id, updateUserDTO.credit());
            try {
                storage.update(USERS, id.id(), newUser);
            } catch (ItemNotPersistedException e) {
                throw new FatalErrorException("Unexpected update of user failed", e);
            }
            return newUser;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Collection<Ride> getRides() {
        try {
            transactionLock.lock();
            return storage.getAllFromCollection(RIDES, Ride.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Optional<Ride> getRide(RideId rideId) {
        try {
            transactionLock.lock();
            return storage.find(RIDES, rideId.id(), Ride.class);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Ride startRide(StartRideDTO startRideDTO)
            throws NotFoundException, UserAlreadyOnRideException, EBikeAlreadyOnRideException {
        try {
            transactionLock.lock();
            var rides = storage.getAllFromCollection(RIDES, Ride.class);
            if (rides.stream().anyMatch(r -> r.userId().equals(startRideDTO.userId()) && r.endDate().isEmpty())) {
                throw new UserAlreadyOnRideException(
                        "The user with id " + startRideDTO.userId().id() + " is already on a ride");
            }
            if (rides.stream().anyMatch(r -> r.ebikeId().equals(startRideDTO.ebikeId()) && r.endDate().isEmpty())) {
                throw new EBikeAlreadyOnRideException(
                        "The ebike with id " + startRideDTO.ebikeId().id() + " is already on a ride");
            }
            if (storage.find(USERS, startRideDTO.userId().id(), User.class).isEmpty()) {
                throw new NotFoundException(
                        "No user with id " + startRideDTO.userId().id() + " was found to start a ride");
            }
            var bike = storage.find(BIKES, startRideDTO.ebikeId().id(), EBike.class)
                    .orElseThrow(() -> new NotFoundException(
                            "No ebike with id " + startRideDTO.ebikeId().id() + " was found to start a ride"));

            if (bike.state() != EBikeState.AVAILABLE) {
                throw new EBikeAlreadyOnRideException("The ebike with id " + bike.id().id() + " is already on a ride");
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
            try {
                storage.update(BIKES, bike.id().id(),
                        new EBike(bike.id(), EBikeState.IN_USE, bike.loc(), bike.direction(),
                                bike.speed(), bike.batteryLevel()));
            } catch (ItemNotPersistedException e) {
                // Should not happen as the bike was retreived
                throw new FatalErrorException("Something went wrong while updating bike state to IN_USE", e);
            }
            return ride;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Ride endRide(RideId rideId)
            throws NotFoundException, RideAlreadyEndedException {
        try {
            transactionLock.lock();
            var optional = storage.find(RIDES, rideId.id(), Ride.class);
            if (optional.isEmpty()) {
                throw new NotFoundException("No ride with id " + rideId.id() + " was found");
            }
            var ride = optional.get();
            if (ride.endDate().isPresent()) {
                throw new RideAlreadyEndedException("The ride is already ended");
            }
            var bike = storage.find(BIKES, ride.ebikeId().id(), EBike.class)
                    .orElseThrow(() -> new FatalErrorException("Did not find the bike associated to that ride"));

            ride = new Ride(ride.id(), ride.startedDate(), Optional.of(new Date()), ride.userId(), ride.ebikeId());
            try {
                storage.update(RIDES, ride.id().id(), ride);
            } catch (ItemNotPersistedException e) {
                // Should not happen as the ride was retreived
                throw new FatalErrorException("Unexpected update of ride failed", e);
            }
            try {
                storage.update(BIKES, bike.id().id(),
                        new EBike(bike.id(), EBikeState.AVAILABLE, bike.loc(), bike.direction(),
                                bike.speed(), bike.batteryLevel()));
            } catch (ItemNotPersistedException e) {
                // Should not happen as the bike was retreived
                throw new FatalErrorException("Something went wrong while updating bike state to AVAILABLE", e);
            }
            return ride;
        } finally {
            transactionLock.unlock();
        }
    }
}
