package com.team_nebula.nebula.domain.star.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarSearchDocument {

    private String id;
    private Long userId;
    private String allContent;
    private String title;
    private String categoryName;
    private String summaryAI;
    private String userMemo;
    private List<String> keywords;
    private String siteUrl;
    private Integer views;
    private String lastAccessedAt;
    private String thumbnailUrl;
    private String faviconUrl;
}
