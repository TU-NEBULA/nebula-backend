package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchedStarOneResponseDTO {
    GetStarOneResponseDTO searchedStar;
    GetLinkOneResponseDTO linkData;
    GetStarOneResponseDTO linkedStar;
}
