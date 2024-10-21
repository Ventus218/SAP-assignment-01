package sap.ass01.solution.backend.hexagonal.domain.model.dto;

import sap.ass01.solution.backend.hexagonal.domain.model.EBikeId;
import sap.ass01.solution.backend.hexagonal.domain.model.P2d;
import sap.ass01.solution.backend.hexagonal.domain.model.V2d;

public record CreateEBikeDTO(
        EBikeId id,
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
