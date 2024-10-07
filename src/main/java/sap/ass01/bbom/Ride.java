package sap.ass01.bbom;

import java.util.*;

public class Ride {

	private Date startedDate;
	private Optional<Date> endDate;
	private User user;
	private EBike ebike;
	private String id;
	private RideSimulation rideSimulation;

	public Ride(String id, User user, EBike ebike) {
		this.id = id;
		this.startedDate = new Date();
		this.endDate = Optional.empty();
		this.user = user;
		this.ebike = ebike;
	}

	public String getId() {
		return id;
	}

	public void start(EBikeApp app) {
		rideSimulation = new RideSimulation(this, user, app);
		RideSimulationControlPanel ridingWindow = new RideSimulationControlPanel(this, app);
		ridingWindow.display();
		rideSimulation.start();

	}

	public void end() {
		endDate = Optional.of(new Date());
		rideSimulation.stopSimulation();
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public boolean isOngoing() {
		return this.endDate.isEmpty();
	}

	public Optional<Date> getEndDate() {
		return endDate;
	}

	public User getUser() {
		return user;
	}

	public EBike getEBike() {
		return ebike;
	}

	public String toString() {
		return "{ id: " + this.id + ", user: " + user.getId() + ", bike: " + ebike.getId() + " }";
	}
}
