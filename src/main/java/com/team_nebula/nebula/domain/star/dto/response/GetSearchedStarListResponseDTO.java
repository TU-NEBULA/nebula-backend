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
public class GetSearchedStarListResponseDTO {
    private String type;
    private int totalStarCnt;
    private int totalLinkCnt;
    private List<GetStarOneResponseDTO> searchedStarListDto;
    private List<GetLinkOneResponseDTO> linkListDto;
    private List<GetStarOneResponseDTO> linkedStarListDto;
}
