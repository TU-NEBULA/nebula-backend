package com.team_nebula.nebula.domain.link.repository;

import com.team_nebula.nebula.domain.link.entity.Link;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface LinkRepository extends Neo4jRepository<Link, Long> {

    @Query("""
        MATCH (s1:Star)-[:HAS_KEYWORD]->(k:Keyword)<-[:HAS_KEYWORD]-(s2:Star)
        WHERE s1.id = $starId AND s1 <> s2
        WITH s1, s2, COUNT(k) AS sharedKeywordNum
        WHERE sharedKeywordNum > 0
        MERGE (l:Link {linked_two_node_Id: [s1.id, s2.id]})
        ON CREATE SET l.sharedKeywordNum = sharedKeywordNum, l.similarityScore = 0.5
        MERGE (s1)-[:LINKED]->(l)
        MERGE (s2)-[:LINKED]->(l)
    """)
    void createLinksBetweenStars(@Param("starId") Long starId);

    @Query("""
        MATCH (s:Star)-[:LINKED]->(l:Link)<-[:LINKED]-(s2:Star)
        WHERE s.userId = $userId OR s2.userId = $userId
        RETURN l, l.linked_two_node_Id AS linkedNodeIdList, 
               l.sharedKeywordNum AS sharedKeywordNum, 
               l.similarityScore AS similarity
    """)
    List<Map<String, Object>> findLinksByUserId(@Param("userId") Long userId);
}
