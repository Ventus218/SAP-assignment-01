package sap.ass01.solution.backend.layered.businesslogic.model.dto;

import sap.ass01.solution.backend.layered.businesslogic.model.*;

public record UpdateEBikeDTO(
        P2d loc,
        V2d direction,
        double speed,
        int batteryLevel) {
}
