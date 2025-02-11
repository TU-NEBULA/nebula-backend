package com.team_nebula.nebula.domain.link.repository;

import com.team_nebula.nebula.domain.link.entity.Link;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LinkRepository extends Neo4jRepository<Link, UUID> {

    @Query("""
        MATCH (s1:Star)-[:TAGGED]->(k:Keyword)<-[:TAGGED]-(s2:Star)
        WHERE s1.id = $starId AND s1 <> s2
        WITH s1, s2, COUNT(k) AS sharedKeywordNum
        WHERE sharedKeywordNum > 0
        MERGE (l:Link {linked_two_node_Id: [s1.id, s2.id]})
        ON CREATE SET l.sharedKeywordNum = sharedKeywordNum, l.similarityScore = 0.5, l.id = randomUUID()
        MERGE (s1)-[:LINKED]->(l)
        MERGE (s2)-[:LINKED]->(l)
    """)
    void createLinksBetweenStars(@Param("starId") UUID starId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)
    WHERE u.userId = $userId

    MATCH (s)-[:LINKED]->(l:Link)
    WHERE EXISTS {
        MATCH (s2:Star)-[:LINKED]->(l)
        WHERE (u)-[:CREATED]->(s2)
    }

    RETURN DISTINCT l.id AS linkId,
                    l.linked_two_node_Id AS linkedNodeIdList,
                    l.sharedKeywordNum AS sharedKeywordNum,
                    l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinksByUserId(@Param("userId") Long userId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
    WHERE u.userId = $userId AND c.categoryId = $categoryId
    
    MATCH (s)-[:LINKED]->(l:Link)<-[:LINKED]-(s2:Star)
    WHERE (u)-[:CREATED]->(s2)
    
    RETURN l,
           l.linked_two_node_Id AS linkedNodeIdList,
           l.sharedKeywordNum AS sharedKeywordNum,
           l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinkInCategory(@Param("userId") Long userId, @Param("categoryId") UUID categoryId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
    WHERE u.userId = $userId AND k.keywordId = $keywordId
    
    MATCH (s)-[:LINKED]->(l:Link)<-[:LINKED]-(s2:Star)
    WHERE (u)-[:CREATED]->(s2)
    
    RETURN l,
           l.linked_two_node_Id AS linkedNodeIdList,
           l.sharedKeywordNum AS sharedKeywordNum,
           l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinkInKeyword(@Param("userId") Long userId, @Param("keywordId") Long keywordId);
}
