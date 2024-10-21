package sap.ass01.solution.backend.hexagonal.domain.model.dto;

import sap.ass01.solution.backend.hexagonal.domain.model.EBikeId;
import sap.ass01.solution.backend.hexagonal.domain.model.UserId;

public record StartRideDTO(UserId userId, EBikeId ebikeId) {
}
