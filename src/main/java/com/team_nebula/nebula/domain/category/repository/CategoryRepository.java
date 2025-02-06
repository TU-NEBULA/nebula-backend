package com.team_nebula.nebula.domain.category.repository;

import com.team_nebula.nebula.domain.category.entity.Category;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends Neo4jRepository<Category, UUID> {

    boolean existsByName(String name);

    @Query("""
    MATCH (u:UserNode {userId: $userId})-[:GENERATED]->(c:Category)
    OPTIONAL MATCH (c)<-[:BELONGS_TO]-(s:Star)
    RETURN c.id AS id, c.name AS name, COUNT(s) AS includedStarCnt
    """)
    List<Map<String, Object>> findUserCategoriesWithStarCount(@Param("userId") Long userId);

    Optional<Category> findByName(String name);

}
