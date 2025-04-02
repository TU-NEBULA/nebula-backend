package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StarRepository extends Neo4jRepository<Star, UUID> {
    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)
    WHERE u.userId = $userId AND s.isDeletedStatus = false
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
    OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)

    RETURN s.id AS starId,
           s.title AS title,
           s.siteUrl AS siteUrl,
           s.thumbnailUrl AS thumbnailUrl,
           s.summaryAI AS summaryAI,
           s.userMemo AS userMemo,
           s.views AS views,
           c.name AS categoryName,
           f.faviconUrl AS faviconUrl,
           COLLECT(k.name) AS keywordList
""")
    List<GetStarOneResponseDTO> findStarsByUserId(@Param("userId") Long userId);


    @Query("""
        MATCH (s:Star {id: $starId})-[:BELONGS_TO]->(c:Category)
        OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
        OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
        WHERE s.isDeletedStatus = false
    
        RETURN s.id AS starId,
               s.title AS title,
               s.siteUrl AS siteUrl,
               s.thumbnailUrl AS thumbnailUrl,
               s.summaryAI AS summaryAI,
               s.userMemo AS userMemo,
               s.views AS views,
               c.name AS categoryName,
               f.faviconUrl AS faviconUrl,
               COLLECT(k.name) AS keywordList
    """)
    GetStarOneResponseDTO findStarDetailById(@Param("starId") UUID starId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
    WHERE u.userId = $userId AND c.id = $categoryId AND s.isDeletedStatus = false
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
    RETURN s.id AS starId,
           s.title AS title,
           s.siteUrl AS siteUrl,
           s.thumbnailUrl AS thumbnailUrl,
           s.summaryAI AS summaryAI,
           s.userMemo AS userMemo,
           s.views AS views,
           c.name AS categoryName,
           f.faviconUrl AS faviconUrl,
           COLLECT(k.name) AS keywordList
    """)
    List<GetStarOneResponseDTO> findStarsInCategory(@Param("userId") Long userId, @Param("categoryId") UUID categoryId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
    WHERE u.userId = $userId AND k.name = $keywordId AND s.isDeletedStatus = false
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
    OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)
    RETURN s.id AS starId,
           s.title AS title,
           s.siteUrl AS siteUrl,
           s.thumbnailUrl AS thumbnailUrl,
           s.summaryAI AS summaryAI,
           s.userMemo AS userMemo,
           s.views AS views,
           c.name AS categoryName,
           f.faviconUrl AS faviconUrl,
           COLLECT(k.name) AS keywordList
    """)
    List<GetStarOneResponseDTO> findStarsInKeyword(@Param("userId") Long userId, @Param("keywordId") String keywordId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)
    WHERE u.userId = $userId
      AND ($title IS NULL OR toLower(s.title) CONTAINS toLower($title))
      AND s.isDeletedStatus = false

    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
    OPTIONAL MATCH (s)-[:HAS_FAVICON]->(f:Favicon)

    OPTIONAL MATCH (s)-[:LINKED]->(l:Link)-[:LINKED]-(s2:Star)
    WHERE s2 <> s
    OPTIONAL MATCH (s2)-[:BELONGS_TO]->(c2:Category)
    OPTIONAL MATCH (s2)-[:TAGGED]->(k2:Keyword)
    OPTIONAL MATCH (s2)-[:HAS_FAVICON]->(f2:Favicon)

    WITH
        s, c, f, COLLECT(DISTINCT k.name) AS keywordList,
        s2, c2, f2, COLLECT(DISTINCT k2.name) AS linkedKeywordList,
        COLLECT(DISTINCT {
            linkId: l.id,
            sharedKeywordNum: l.sharedKeywordNum,
            similarity: l.similarityScore
        }) AS links

    RETURN
        COLLECT(DISTINCT {
            searchedStar: {
                starId: s.id,
                categoryName: c.name,
                title: s.title,
                siteUrl: s.siteUrl,
                thumbnailUrl: s.thumbnailUrl,
                summaryAI: s.summaryAI,
                userMemo: s.userMemo,
                views: s.views,
                faviconUrl: f.faviconUrl,
                keywordList: keywordList
            },
            linkData: links,
            linkedStar: {
                starId: s2.id,
                categoryName: c2.name,
                title: s2.title,
                siteUrl: s2.siteUrl,
                thumbnailUrl: s2.thumbnailUrl,
                summaryAI: s2.summaryAI,
                userMemo: s2.userMemo,
                views: s2.views,
                faviconUrl: f2.faviconUrl,
                keywordList: linkedKeywordList
            }
        }) AS result
    """)
    List<GetSearchedStarOneResponseDTO> searchStars(@Param("userId") Long userId, @Param("title") String title);

    @Query("""
            MATCH (s:Star)
            WHERE s.id = $starId
            SET s.views = s.views + 1
            """)
    void incrementViews(UUID starId);

    @Query("""
            MATCH (s:Star)
            WHERE s.id = $starId
            SET s.views = s.views - 1
            """)
    void reduceViews(UUID starId);
}
