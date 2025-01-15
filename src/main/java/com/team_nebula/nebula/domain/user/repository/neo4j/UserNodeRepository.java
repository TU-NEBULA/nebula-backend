package com.team_nebula.nebula.domain.user.repository.neo4j;

import com.team_nebula.nebula.domain.user.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
}
