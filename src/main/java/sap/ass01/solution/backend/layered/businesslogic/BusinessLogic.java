package sap.ass01.solution.backend.layered.businesslogic;

import java.util.*;
import sap.ass01.solution.backend.layered.businesslogic.exceptions.*;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.*;

public interface BusinessLogic {

    public void startTransaction() throws InterruptedException;

    public void endTransaction();

    public Collection<EBike> getEBikes() throws InterruptedException;

    public EBike createEBike(CreateEBikeDTO createEBikeDTO) throws IllegalArgumentException, InterruptedException;

    public Optional<EBike> getEBike(EBikeId ebikeId) throws InterruptedException;

    public void deleteEBike(EBikeId ebikeId) throws NotFoundException, InterruptedException;

    public EBike updateEBike(EBikeId ebikeId, UpdateEBikeDTO updateEBikeDTO)
            throws NotFoundException, InterruptedException;

    public Collection<User> getUsers() throws InterruptedException;

    public User signup(CreateUserDTO createUserDTO) throws IllegalArgumentException, InterruptedException;

    public User login(UserId userId) throws NotFoundException, InterruptedException;

    public Optional<User> getUser(UserId userId) throws InterruptedException;

    public User updateUser(UserId id, UpdateUserDTO updateUserDTO) throws NotFoundException, InterruptedException;

    public Collection<Ride> getRides() throws InterruptedException;

    public Optional<Ride> getRide(RideId rideId) throws InterruptedException;

    public Ride startRide(StartRideDTO startRideDTO)
            throws NotFoundException, UserAlreadyOnRideException, EBikeAlreadyOnRideException, InterruptedException;

    public Ride endRide(EndRideDTO endRideDTO)
            throws NotFoundException, RideAlreadyEndedException, InterruptedException;

}
