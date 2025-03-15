package com.team_nebula.nebula.global.AI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetThumbnailAndKeywordsResponseDTO {
    private String image_url;
    private List<String> keywords;
}
