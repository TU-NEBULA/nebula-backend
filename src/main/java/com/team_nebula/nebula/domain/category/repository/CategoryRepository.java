package com.team_nebula.nebula.domain.category.repository;

import com.team_nebula.nebula.domain.category.entity.Category;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CategoryRepository extends Neo4jRepository<Category, Long> {
}
