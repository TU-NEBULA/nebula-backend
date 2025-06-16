package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.dto.response.GetCategoryKeywordStarRawDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StarNeo4jRepositoryCustomImpl implements StarNeo4jRepositoryCustom{

    private final Neo4jClient neo4jClient;

    @Override
    public List<GetCategoryKeywordStarRawDTO> fetchRawCategoryKeywordStarData(Long userId) {
        String query = """
            MATCH (u:UserNode {userId: $userId})-[:GENERATED]->(c:Category)
            WHERE c.isDeletedStatus = false

            OPTIONAL MATCH (c)<-[:BELONGS_TO]-(s:Star)
            WHERE s.isDeletedStatus = false

            OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
            OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)

            RETURN c.name AS categoryName,
                   k.name AS keywordName,
                   s.id AS starId,
                   s.title AS title,
                   s.siteUrl AS siteUrl,
                   s.thumbnailUrl AS thumbnailUrl,
                   s.summaryAI AS summaryAI,
                   s.userMemo AS userMemo,
                   s.views AS views,
                   f.faviconUrl AS faviconUrl,
                   s.lastAccessedAt AS lastAccessedAt
        """;

        return neo4jClient.query(query)
                .bind(userId).to("userId")
                .fetchAs(GetCategoryKeywordStarRawDTO.class)
                .mappedBy((typeSystem, record) -> GetCategoryKeywordStarRawDTO.builder()
                        .categoryName(record.get("categoryName").asString(null))
                        .keywordName(record.get("keywordName").asString(null))
                        .starId(record.get("starId").isNull() ? null : UUID.fromString(record.get("starId").asString()))
                        .title(record.get("title").asString(null))
                        .siteUrl(record.get("siteUrl").asString(null))
                        .thumbnailUrl(record.get("thumbnailUrl").asString(null))
                        .summaryAI(record.get("summaryAI").asString(null))
                        .userMemo(record.get("userMemo").asString(null))
                        .views(record.get("views").asInt(0))
                        .faviconUrl(record.get("faviconUrl").asString(null))
                        .lastAccessedAt(record.get("lastAccessedAt").asOffsetDateTime(null))
                        .build())
                .all()
                .stream()
                .toList();
    }
}
