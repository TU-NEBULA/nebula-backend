package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StarRepository extends Neo4jRepository<Star, UUID> {
    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)
    WHERE u.userId = $userId
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
    RETURN s.id AS starId,
           s.title AS title,
           s.siteUrl AS siteUrl,
           s.thumbnailUrl AS thumbnailUrl,
           s.summaryAI AS summaryAI,
           s.userMemo AS userMemo,
           s.views AS views,
           c.name AS categoryName,
           COLLECT(k.name) AS keywordList
""")
    List<GetStarOneResponseDTO> findStarsByUserId(@Param("userId") Long userId);


    @Query("""
        MATCH (s:Star)-[:BELONGS_TO]->(c:Category)
        OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
        WHERE ID(s) = $starId
        RETURN s.id AS starId, 
               s.title AS title, 
               s.siteUrl AS siteUrl, 
               s.thumbnailUrl AS thumbnailUrl, 
               s.summaryAI AS summaryAI, 
               s.userMemo AS userMemo, 
               s.views AS views, 
               c.name AS categoryName, 
               COLLECT(k.name) AS keywordList
    """)
    GetStarOneResponseDTO findStarDetailById(@Param("starId") UUID starId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
    WHERE u.userId = $userId AND c.categoryId = $categoryId
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    RETURN s.id AS starId, 
           s.title AS title, 
           s.siteUrl AS siteUrl, 
           s.thumbnailUrl AS thumbnailUrl, 
           s.summaryAI AS summaryAI, 
           s.userMemo AS userMemo, 
           s.views AS views, 
           c.name AS categoryName, 
           COLLECT(k.name) AS keywordList
    """)
    List<GetStarOneResponseDTO> findStarsInCategory(@Param("userId") Long userId, @Param("categoryId") UUID categoryId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
    WHERE u.userId = $userId AND k.keywordId = $keywordId
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
    RETURN s.id AS starId, 
           s.title AS title, 
           s.siteUrl AS siteUrl, 
           s.thumbnailUrl AS thumbnailUrl, 
           s.summaryAI AS summaryAI, 
           s.userMemo AS userMemo, 
           s.views AS views, 
           c.name AS categoryName, 
           COLLECT(k.name) AS keywordList
    """)
    List<GetStarOneResponseDTO> findStarsInKeyword(@Param("userId") Long userId, @Param("keywordId") Long keywordId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)
    WHERE u.userId = $userId
      AND ($title IS NULL OR toLower(s.title) CONTAINS toLower($title))
    
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)

    OPTIONAL MATCH (s)-[:LINKED]->(l:Link)-[:LINKED]-(s2:Star)
    WHERE s2 <> s
    
    RETURN 
        COLLECT(DISTINCT {
            starId: ID(s),
            categoryName: c.name,
            title: s.title,
            siteUrl: s.siteUrl,
            thumbnailUrl: s.thumbnailUrl,
            summaryAI: s.summaryAI,
            userMemo: s.userMemo,
            views: s.views,
            keywordList: COLLECT(DISTINCT k.name)
        }) AS stars,
        
        COLLECT(DISTINCT {
            linkId: ID(l),
            linkedNodeIdList: [ID(s), ID(s2)],
            sharedKeywordNum: l.sharedKeywordNum,
            similarity: l.similarityScore
        }) AS links
""")
    List<Map<String, Object>> searchStars(@Param("userId") Long userId, @Param("title") String title);
}
