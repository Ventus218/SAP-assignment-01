package sap.ass01.solution.frontend.model.dto;

import sap.ass01.solution.frontend.model.*;

public record CreateEBikeDTO(
        EBikeId id,
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
