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
public class GetLinkOneResponseDTO {
    private Long linkId;
    private int sharedKeywordNum;
    private double similarity;
    private List<Long> linkedNodeIdList;
}
