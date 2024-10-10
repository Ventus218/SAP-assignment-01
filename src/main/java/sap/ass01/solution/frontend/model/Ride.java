package sap.ass01.solution.frontend.model;

import java.util.*;

public record Ride(
        Date startedDate,
        Optional<Date> endDate,
        User user,
        EBike ebike,
        String id) {
}
