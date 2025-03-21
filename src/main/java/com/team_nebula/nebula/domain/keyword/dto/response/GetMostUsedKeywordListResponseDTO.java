package com.team_nebula.nebula.domain.keyword.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GetMostUsedKeywordListResponseDTO {
    private List<GetMostUsedKeywordOneResponseDTO> mostUsedKeywordList;
}
