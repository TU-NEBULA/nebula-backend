package com.team_nebula.nebula.domain.star.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Get2DStarOneResponseDTO {
    private UUID starId;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
    private Integer views;
    private String faviconUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime lastAccessedAt;
}
