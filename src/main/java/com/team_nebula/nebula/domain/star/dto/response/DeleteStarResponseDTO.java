package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStarResponseDTO {
    private UUID starId;
    private String deleteStatus;
}
