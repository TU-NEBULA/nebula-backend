package com.team_nebula.nebula.domain.star.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeHtmlRequestDTO {
    private Long userId;
    private String s3Key;
}
