package com.team_nebula.nebula.domain.star.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStarOneRequestDTO {
    String title;
    String categoryName;
    String summaryAI;
    String userMemo;
}
