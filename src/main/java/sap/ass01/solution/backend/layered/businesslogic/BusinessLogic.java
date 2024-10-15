package sap.ass01.solution.backend.layered.businesslogic;

import java.util.*;
import sap.ass01.solution.backend.layered.businesslogic.exceptions.NotFoundException;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.*;

public interface BusinessLogic {

    public Collection<EBike> getEBikes();

    public EBike createEBike(CreateEBikeDTO createEBikeDTO) throws IllegalArgumentException;

    public Optional<EBike> getEBike(EBikeId ebikeId);

    public void deleteEBike(EBikeId ebikeId) throws NotFoundException;

    public Collection<User> getUsers();

    public User signup(CreateUserDTO createUserDTO) throws IllegalArgumentException;

    public User login(UserId userId) throws NotFoundException;

    public Optional<User> getUser(UserId userId);

    public Collection<Ride> getRides();

    public Optional<Ride> getRide(RideId rideId);

    public Ride startRide(StartRideDTO startRideDTO) throws NotFoundException;

    public Ride endRide(EndRideDTO endRideDTO) throws NotFoundException;

}
