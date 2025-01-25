package com.team_nebula.nebula.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryOneResponseDTO {
    private Long id;
    private String name;
    private int includedStarCnt;
}
