package com.team_nebula.nebula.domain.keyword.repository;

import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface KeywordRepository extends Neo4jRepository<Keyword, Long> {
}
