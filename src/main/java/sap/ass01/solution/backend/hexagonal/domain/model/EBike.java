package sap.ass01.solution.backend.hexagonal.domain.model;

public record EBike(
        EBikeId id,
        EBikeState state,
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
