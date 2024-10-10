package sap.ass01.solution.frontend.model;

import sap.ass01.bbom.P2d;
import sap.ass01.bbom.V2d;

public record EBike(
        String id,
        EBikeState state,
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
