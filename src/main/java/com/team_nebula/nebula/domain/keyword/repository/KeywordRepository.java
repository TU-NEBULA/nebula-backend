package com.team_nebula.nebula.domain.keyword.repository;

import com.team_nebula.nebula.domain.keyword.dto.response.GetMostUsedKeywordOneResponseDTO;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KeywordRepository extends Neo4jRepository<Keyword, String> {
    @Query("""
        UNWIND apoc.coll.toSet($keywordNames) AS keywordName
        MATCH (s:Star {id: $starId})
        MERGE (k:Keyword {name: keywordName})
        WITH s, k
        MERGE (s)-[:TAGGED]->(k);
    """)
    void linkStarToKeywords(@Param("starId") UUID starId, @Param("keywordNames") List<String> keywordNames);

    @Query("""
        MATCH (s:Star {id: $starId})-[t:TAGGED]->(:Keyword)
        DELETE t
    """)
    void removeOldKeywords(@Param("starId") UUID starId);

    @Query("""
        MATCH (k:Keyword)
        WHERE NOT (k)<-[:TAGGED]-(:Star)
        WITH COLLECT(k.name) AS orphanKeywordNames, k
        DELETE k
        RETURN orphanKeywordNames
    """)
    List<String> removeOrphanKeywords();

    @Query("""
        MATCH (u:UserNode {userId: $userId})-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
        RETURN DISTINCT k.name
    """)
    List<String> getAllKeywordNames(@Param("userId") Long userId);

    @Query("""
    MATCH (k:Keyword)<-[:TAGGED]-(s:Star)
    WHERE (s.isDeletedStatus = false OR s.isDeletedStatus IS NULL)
    RETURN k.name AS keywordName, COUNT(s) AS usedCnt
    ORDER BY usedCnt DESC, keywordName ASC
    LIMIT 10
    """)
    List<GetMostUsedKeywordOneResponseDTO> getMostUsedKeywordList();


}
