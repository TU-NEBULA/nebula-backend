package com.team_nebula.nebula.domain.favicon.repository;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FaviconRepository extends Neo4jRepository<Favicon, String> {
}
