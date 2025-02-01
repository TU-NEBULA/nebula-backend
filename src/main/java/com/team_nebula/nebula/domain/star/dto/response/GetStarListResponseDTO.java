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
public class GetStarListResponseDTO {
    private int totalStarCnt;
    private int totalLinkCnt;
    private List<GetStarOneResponseDTO> starListDto;
    private List<GetLinkOneResponseDTO> linkListDto;
}
