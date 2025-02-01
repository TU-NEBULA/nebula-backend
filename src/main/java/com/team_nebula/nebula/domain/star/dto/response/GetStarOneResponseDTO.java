package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetStarOneResponseDTO {
    private Long linkId;
    private int sharedKeywordNum;
    private double similarityScore;
    private List<Long> linkedNodeIdList;
}
