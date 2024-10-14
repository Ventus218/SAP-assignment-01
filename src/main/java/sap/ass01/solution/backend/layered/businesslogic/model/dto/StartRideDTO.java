package sap.ass01.solution.backend.layered.businesslogic.model.dto;

import sap.ass01.solution.backend.layered.businesslogic.model.EBikeId;
import sap.ass01.solution.backend.layered.businesslogic.model.UserId;

public record StartRideDTO(UserId userId, EBikeId ebikeId) {
}
