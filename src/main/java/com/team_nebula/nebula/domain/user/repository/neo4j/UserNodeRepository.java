package com.team_nebula.nebula.domain.user.repository.neo4j;

import com.team_nebula.nebula.domain.user.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {

    @Query("MATCH (u:UserNode {userId: $userId}) RETURN u")
    Optional<UserNode> findByUserId(Long userId);
}
