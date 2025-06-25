package com.team_nebula.nebula.domain.star.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.List;

@Document(indexName = "star_search")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarSearchDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Keyword)
    private String siteUrl;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String summaryAI;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String userMemo;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    @Field(type = FieldType.Integer)
    private Integer views;

    @Field(type = FieldType.Date)
    private OffsetDateTime lastAccessedAt;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(type = FieldType.Keyword)
    private String faviconUrl;

    // 통합 검색 필드
    @Field(type = FieldType.Text, analyzer = "standard")
    private String allContent;
}
