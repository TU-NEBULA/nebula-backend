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
public class AddBookMarkResponseDTO {
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String faviconUrl;
    private List<String> keywords;
    private String s3key;
}
