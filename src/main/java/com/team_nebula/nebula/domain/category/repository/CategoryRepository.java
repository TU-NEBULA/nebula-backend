package com.team_nebula.nebula.domain.category.repository;

import com.team_nebula.nebula.domain.category.dto.response.GetCategoryOneResponseDTO;
import com.team_nebula.nebula.domain.category.entity.Category;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends Neo4jRepository<Category, UUID> {

    boolean existsByName(String name);

    @Query("""
    MATCH (u:UserNode {userId: $userId})-[:GENERATED]->(c:Category)
    WHERE c.isDeletedStatus = false OR c.isDeletedStatus IS NULL
    OPTIONAL MATCH (c)<-[:BELONGS_TO]-(s:Star)
    RETURN c.id AS id, c.name AS name, COUNT(s) AS includedStarCnt
    """)
    List<GetCategoryOneResponseDTO> findUserCategoriesWithStarCount(@Param("userId") Long userId);

    @Query("""
    MATCH (u:UserNode {userId: $userId})-[:GENERATED]->(c:Category {name: $name})
    WHERE c.isDeletedStatus = false OR c.isDeletedStatus IS NULL
    RETURN c
    LIMIT 1
            """)
    Optional<Category> findByNameAndUserId(String name, Long userId);

    @Query("""
    MATCH (c:Category)<-[:BELONGS_TO]-(s:Star{id: $starId})
    RETURN c.name
    """)
    String findByStar(UUID starId);

    @Query("""
    MATCH (s:Star {id: $starId})-[r:BELONGS_TO]->(c:Category)
    DELETE r
    WITH s
    MATCH (newCategory:Category {name: $categoryName})
    MERGE (s)-[:BELONGS_TO]->(newCategory)
    RETURN newCategory.name
    """)
    String findNameByStarAndRemoveRelation(@Param("starId") UUID starId, @Param("categoryName") String categoryName);

}
