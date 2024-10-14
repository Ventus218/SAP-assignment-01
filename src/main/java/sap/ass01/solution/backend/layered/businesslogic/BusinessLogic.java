package sap.ass01.solution.backend.layered.businesslogic;

import java.util.Collection;
import sap.ass01.solution.backend.layered.businesslogic.model.*;
import sap.ass01.solution.backend.layered.businesslogic.model.dto.*;

public interface BusinessLogic {

    public Collection<EBike> getEBikes();

    public void createEBike(CreateEBikeDTO createEBikeDTO);

    public EBike getEBike(EBikeId ebikeId);

    public void deleteEBike(EBikeId ebikeId);

    public Collection<User> getUsers();

    public void signup(CreateUserDTO createUserDTO);

    public void login(UserId userId);

    public User getUser(UserId userId);

    public Collection<Ride> getRides();

    public Ride getRide(RideId rideId);

    public void startRide(StartRideDTO startRideDTO);

    public void endRide(EndRideDTO endRideDTO);

}
