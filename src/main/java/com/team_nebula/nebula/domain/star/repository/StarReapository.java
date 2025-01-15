package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.entity.Star;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface StarReapository extends Neo4jRepository<Star, Long> {
}
