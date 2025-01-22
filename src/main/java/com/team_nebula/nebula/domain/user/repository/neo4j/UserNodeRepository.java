package com.team_nebula.nebula.domain.user.repository.neo4j;

import com.team_nebula.nebula.domain.user.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface UserNodeRepository extends Neo4jRepository<UserNode, String> {

    @Query("MATCH (u:UserNode {username: $username}) RETURN u")
    UserNode findByUsername(String username);
}
