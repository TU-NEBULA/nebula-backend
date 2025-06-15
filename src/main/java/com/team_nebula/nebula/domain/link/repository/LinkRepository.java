package com.team_nebula.nebula.domain.link.repository;

import com.team_nebula.nebula.domain.link.entity.Link;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LinkRepository extends Neo4jRepository<Link, UUID> {

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s1:Star)-[:TAGGED]->(k:Keyword)<-[:TAGGED]-(s2:Star)<-[:CREATED]-(u)
    WHERE u.userId = $userId AND s1.id = $starId AND s1 <> s2
    AND (s1.isDeletedStatus = false OR s1.isDeletedStatus IS NULL)
    AND (s2.isDeletedStatus = false OR s2.isDeletedStatus IS NULL)
    
    WITH s1, s2, COLLECT(DISTINCT k.name) AS sharedKeywords, COUNT(DISTINCT k) AS sharedKeywordNum
    WHERE sharedKeywordNum > 0

    MERGE (l:Link {linked_two_node_Id: [s1.id, s2.id]})
    ON CREATE SET
        l.id = randomUUID(),
        l.sharedKeywordNum = sharedKeywordNum,
        l.similarityScore = 0.5,
        l.sharedKeywords = sharedKeywords

    MERGE (s1)-[:LINKED]->(l)
    MERGE (s2)-[:LINKED]->(l)
    """)
    void createLinksBetweenStars(@Param("userId") Long userId, @Param("starId") UUID starId);


    @Query("""
    MATCH (u:UserNode {userId: $userId})-[:CREATED]->(s:Star)
    WHERE s.isDeletedStatus = false OR s.isDeletedStatus IS NULL
   
    MATCH (s)-[:LINKED]-(l:Link)-[:LINKED]-(s2:Star)
    WHERE (u)-[:CREATED]->(s2) AND (s2.isDeletedStatus = false OR s2.isDeletedStatus IS NULL)
   
    OPTIONAL MATCH (s)-[:TAGGED]->(k:Keyword)<-[:TAGGED]-(s2)
   
    RETURN DISTINCT l.id AS linkId,
                   l.linked_two_node_Id AS linkedNodeIdList,
                   l.sharedKeywordNum AS sharedKeywordNum,
                   COLLECT(DISTINCT k.name) AS sharedKeywords,
                   l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinksByUserId(@Param("userId") Long userId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:BELONGS_TO]->(c:Category)
    WHERE u.userId = $userId AND c.id = $categoryId AND (s.isDeletedStatus = false OR s.isDeletedStatus IS NULL)
    
    MATCH (s)-[:LINKED]->(l:Link)<-[:LINKED]-(s2:Star)
    WHERE (u)-[:CREATED]->(s2) AND (s2.isDeletedStatus = false OR s2.isDeletedStatus IS NULL)
    
    MATCH (s)-[:TAGGED]->(k:Keyword)<-[:TAGGED]-(s2)

    
    RETURN l.id AS linkId,
           l.linked_two_node_Id AS linkedNodeIdList,
           l.sharedKeywordNum AS sharedKeywordNum,
           COLLECT(DISTINCT k.name) AS sharedKeywords,
           l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinkInCategory(@Param("userId") Long userId, @Param("categoryId") UUID categoryId);

    @Query("""
    MATCH (u:UserNode)-[:CREATED]->(s:Star)-[:TAGGED]->(k:Keyword)
    WHERE u.userId = $userId AND k.name = $keywordId AND (s.isDeletedStatus = false OR s.isDeletedStatus IS NULL)
    
    MATCH (s)-[:LINKED]->(l:Link)<-[:LINKED]-(s2:Star)
    WHERE (u)-[:CREATED]->(s2) AND (s2.isDeletedStatus = false OR s2.isDeletedStatus IS NULL)
    
    MATCH (s)-[:TAGGED]->(kShared:Keyword)<-[:TAGGED]-(s2)

    
    RETURN l,
           l.linked_two_node_Id AS linkedNodeIdList,
           l.sharedKeywordNum AS sharedKeywordNum,
           COLLECT(DISTINCT kShared.name) AS sharedKeywords,
           l.similarityScore AS similarity
    """)
    List<GetLinkOneResponseDTO> findLinkInKeyword(@Param("userId") Long userId, @Param("keywordId") String keywordId);

    @Query("""
    MATCH (s1:Star)-[r1:LINKED]->(l:Link)<-[r2:LINKED]-(s2)
    WHERE s1.id = $starId
    DELETE r1, r2
    WITH l
    WHERE l IS NOT NULL
    DETACH DELETE l
    """)
    void deleteOldLinks(@Param("starId") UUID starId);
}
