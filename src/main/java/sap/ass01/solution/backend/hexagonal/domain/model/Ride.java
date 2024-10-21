package sap.ass01.solution.backend.hexagonal.domain.model;

import java.util.*;

public record Ride(
                RideId id,
                Date startedDate,
                Optional<Date> endDate,
                UserId userId,
                EBikeId ebikeId) {
}
