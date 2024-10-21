package sap.ass01.solution.backend.hexagonal.domain;

import java.util.*;
import java.util.concurrent.locks.*;
import sap.ass01.solution.backend.hexagonal.ports.persistence.Repository;
import sap.ass01.solution.backend.hexagonal.ports.persistence.exceptions.*;
import sap.ass01.solution.backend.hexagonal.domain.exceptions.*;
import sap.ass01.solution.backend.hexagonal.domain.model.*;
import sap.ass01.solution.backend.hexagonal.domain.model.dto.*;

public class DomainImpl implements Domain {

    private final Repository<User> userRepository;
    private final Repository<EBike> ebikesRepository;
    private final Repository<Ride> rideRepository;
    private final Lock transactionLock = new ReentrantLock(true);

    public DomainImpl(Repository<User> userRepository, Repository<EBike> ebikesRepository,
            Repository<Ride> rideRepository) {
        this.userRepository = userRepository;
        this.ebikesRepository = ebikesRepository;
        this.rideRepository = rideRepository;
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
            return ebikesRepository.getAll();
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
                ebikesRepository.insert(createEBikeDTO.id(), ebike);
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
            return ebikesRepository.find(ebikeId);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public void deleteEBike(EBikeId ebikeId) throws NotFoundException {
        try {
            transactionLock.lock();
            try {
                ebikesRepository.delete(ebikeId);
            } catch (NotInRepositoryException e) {
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
                ebikesRepository.update(ebikeId, newBike);
            } catch (NotInRepositoryException e) {
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
            return userRepository.getAll();
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
                userRepository.insert(createUserDTO.id(), user);
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
            var user = userRepository.find(userId);
            return user.orElseThrow(() -> new NotFoundException());
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Optional<User> getUser(UserId userId) {
        try {
            transactionLock.lock();
            return userRepository.find(userId);
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
                userRepository.update(id, newUser);
            } catch (NotInRepositoryException e) {
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
            return rideRepository.getAll();
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Optional<Ride> getRide(RideId rideId) {
        try {
            transactionLock.lock();
            return rideRepository.find(rideId);
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public Ride startRide(StartRideDTO startRideDTO)
            throws NotFoundException, UserAlreadyOnRideException, EBikeAlreadyOnRideException {
        try {
            transactionLock.lock();
            var rides = rideRepository.getAll();
            if (rides.stream().anyMatch(r -> r.userId().equals(startRideDTO.userId()) && r.endDate().isEmpty())) {
                throw new UserAlreadyOnRideException(
                        "The user with id " + startRideDTO.userId().id() + " is already on a ride");
            }
            if (rides.stream().anyMatch(r -> r.ebikeId().equals(startRideDTO.ebikeId()) && r.endDate().isEmpty())) {
                throw new EBikeAlreadyOnRideException(
                        "The ebike with id " + startRideDTO.ebikeId().id() + " is already on a ride");
            }
            if (userRepository.find(startRideDTO.userId()).isEmpty()) {
                throw new NotFoundException(
                        "No user with id " + startRideDTO.userId().id() + " was found to start a ride");
            }
            var bike = ebikesRepository.find(startRideDTO.ebikeId())
                    .orElseThrow(() -> new NotFoundException(
                            "No ebike with id " + startRideDTO.ebikeId().id() + " was found to start a ride"));

            if (bike.state() != EBikeState.AVAILABLE) {
                throw new EBikeAlreadyOnRideException("The ebike with id " + bike.id().id() + " is already on a ride");
            }

            var id = new RideId(UUID.randomUUID().toString());
            var ride = new Ride(id, new Date(), Optional.empty(), startRideDTO.userId(), startRideDTO.ebikeId());
            try {
                rideRepository.insert(id, ride);
            } catch (DuplicateIdException e) {
                // Should not happen since we are using random generated UUIDs;
                throw new FatalErrorException("A randomly generated UUID for a new ride collided with existing ride",
                        e);
            }
            try {
                ebikesRepository.update(bike.id(), new EBike(bike.id(), EBikeState.IN_USE, bike.loc(), bike.direction(),
                        bike.speed(), bike.batteryLevel()));
            } catch (NotInRepositoryException e) {
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
            var optional = rideRepository.find(rideId);
            if (optional.isEmpty()) {
                throw new NotFoundException("No ride with id " + rideId.id() + " was found");
            }
            var ride = optional.get();
            if (ride.endDate().isPresent()) {
                throw new RideAlreadyEndedException("The ride is already ended");
            }
            var bike = ebikesRepository.find(ride.ebikeId())
                    .orElseThrow(() -> new FatalErrorException("Did not find the bike associated to that ride"));

            ride = new Ride(ride.id(), ride.startedDate(), Optional.of(new Date()), ride.userId(), ride.ebikeId());
            try {
                rideRepository.update(ride.id(), ride);
            } catch (NotInRepositoryException e) {
                // Should not happen as the ride was retreived
                throw new FatalErrorException("Unexpected update of ride failed", e);
            }
            try {
                ebikesRepository.update(bike.id(),
                        new EBike(bike.id(), EBikeState.AVAILABLE, bike.loc(), bike.direction(),
                                bike.speed(), bike.batteryLevel()));
            } catch (NotInRepositoryException e) {
                // Should not happen as the bike was retreived
                throw new FatalErrorException("Something went wrong while updating bike state to AVAILABLE", e);
            }
            return ride;
        } finally {
            transactionLock.unlock();
        }
    }
}
