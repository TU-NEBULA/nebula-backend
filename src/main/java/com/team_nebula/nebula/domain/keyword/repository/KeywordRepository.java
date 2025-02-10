package com.team_nebula.nebula.domain.keyword.repository;

import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KeywordRepository extends Neo4jRepository<Keyword, Long> {
    @Query("""
        UNWIND $keywordNames AS keywordName
        MERGE (k:Keyword {name: keywordName})
        WITH k
        MATCH (s:Star {id: $starId})
        MERGE (s)-[:TAGGED]->(k)
        RETURN s
    """)
    Star linkStarToKeywords(@Param("starId") UUID starId, @Param("keywordNames") List<String> keywordNames);
}
