package com.team_nebula.nebula.domain.keyword.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GetMostUsedKeywordOneResponseDTO {
    private String keywordName;
    private int usedCnt;
}
