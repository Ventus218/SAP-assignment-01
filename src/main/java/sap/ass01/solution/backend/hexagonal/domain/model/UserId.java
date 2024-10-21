package sap.ass01.solution.backend.hexagonal.domain.model;

public record UserId(String id) implements EntityId<User> {

    @Override
    public Class<User> type() {
        return User.class;
    }
}
