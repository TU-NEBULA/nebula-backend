package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

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
}
