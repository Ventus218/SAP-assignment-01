package sap.ass01.solution.backend.layered.businesslogic.model.dto;

import sap.ass01.solution.backend.layered.businesslogic.model.EBikeId;
import sap.ass01.solution.backend.layered.businesslogic.model.P2d;
import sap.ass01.solution.backend.layered.businesslogic.model.V2d;

public record CreateEBikeDTO(
        EBikeId id,
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
