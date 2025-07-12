package com.team_nebula.nebula.domain.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrendingKeywordDTO {
    private String keyword;
    
    private Double score;
    
    private Integer growth;
    
    private Integer frequency;
} 