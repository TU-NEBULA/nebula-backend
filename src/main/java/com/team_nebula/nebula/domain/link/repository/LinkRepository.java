package com.team_nebula.nebula.domain.link.repository;

import com.team_nebula.nebula.domain.link.entity.Link;
import jakarta.persistence.Id;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LinkRepository extends Neo4jRepository<Link, Long> {
}
