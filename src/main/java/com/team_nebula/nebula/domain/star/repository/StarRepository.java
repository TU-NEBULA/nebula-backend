package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StarRepository extends Neo4jRepository<Star, Long> {
    @Query("""
        MATCH (u:UserNode)-[:CREATED]->(s:Star)
        WHERE u.userId = $userId
        OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
        OPTIONAL MATCH (s)-[:BELONGS_TO]->(c:Category)
        RETURN s, 
               c.name AS categoryName, 
               COLLECT(k.name) AS keywordList
    """)
    List<Map<String, Object>> findStarsByUserId(@Param("userId") Long userId);

    @Query("""
        MATCH (s:Star)-[:BELONGS_TO]->(c:Category)
        OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
        WHERE ID(s) = $starId
        RETURN s AS s, c.name AS categoryName, COLLECT(k.name) AS keywordList
    """)
    Optional<Map<String, Object>> findStarDetailById(@Param("starId") Long starId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
    WHERE u.userId = $userId AND c.categoryId = $categoryId
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)
    RETURN s, 
           c.name AS categoryName, 
           COLLECT(k.name) AS keywordList
    """)
    List<Map<String, Object>> findStarsInCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
