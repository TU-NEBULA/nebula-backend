package com.team_nebula.nebula.domain.category.repository;

import com.team_nebula.nebula.domain.category.entity.Category;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface CategoryRepository extends Neo4jRepository<Category, Long> {
    boolean existsByName(String name);

    @Query("""
    MATCH (u:UserNode {username: $username})-[:GENERATED]->(c:Category)
    OPTIONAL MATCH (c)<-[:BELONGS_TO]-(s:Star)
    RETURN c.id AS id, c.name AS name, COUNT(s) AS includedStarCnt
    """)
    List<Map<String, Object>> findUserCategoriesWithStarCount(@Param("username") String username);

}
