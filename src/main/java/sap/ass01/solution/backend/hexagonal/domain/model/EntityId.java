package sap.ass01.solution.backend.hexagonal.domain.model;

public interface EntityId<T> {
    public String id();

    public Class<T> type();
}
