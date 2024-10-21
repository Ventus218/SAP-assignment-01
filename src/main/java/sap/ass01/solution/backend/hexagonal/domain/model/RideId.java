package sap.ass01.solution.backend.hexagonal.domain.model;

public record RideId(String id) implements EntityId<Ride> {

    @Override
    public Class<Ride> type() {
        return Ride.class;
    }
}
