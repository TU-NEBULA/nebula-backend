package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetLinkOneResponseDTO {
    private UUID linkId;
    private int sharedKeywordNum;
    private double similarity;
    private List<UUID> linkedNodeIdList;
}
