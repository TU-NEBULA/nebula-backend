package com.team_nebula.nebula.domain.star.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.annotation.Id;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "star_search")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarSearchDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String allContent;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String categoryName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String summaryAI;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String userMemo;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    @Field(type = FieldType.Keyword, index = false)
    private String siteUrl;

    @Field(type = FieldType.Integer, index = false)
    private Integer views;

    @Field(type = FieldType.Keyword, index = false)
    private String lastAccessedAt;

    @Field(type = FieldType.Keyword, index = false)
    private String thumbnailUrl;

    @Field(type = FieldType.Keyword, index = false)
    private String faviconUrl;
}
