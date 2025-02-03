package com.team_nebula.nebula.domain.keyword.repository;

import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends Neo4jRepository<Keyword, Long> {
    @Query("""
        UNWIND $keywordNames AS keywordName
        MERGE (k:Keyword {name: keywordName})
        WITH k
        MATCH (s:Star {id: $starId})
        MERGE (s)-[:TAGGED]->(k)
    """)
    void linkStarToKeywords(@Param("starId") Long starId, @Param("keywordNames") List<String> keywordNames);
}
