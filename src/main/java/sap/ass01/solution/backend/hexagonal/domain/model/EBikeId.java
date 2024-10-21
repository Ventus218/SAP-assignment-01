package sap.ass01.solution.backend.hexagonal.domain.model;

public record EBikeId(String id) implements EntityId<EBike> {

    @Override
    public Class<EBike> type() {
        return EBike.class;
    }
}
