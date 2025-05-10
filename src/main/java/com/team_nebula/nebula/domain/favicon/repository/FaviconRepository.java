package com.team_nebula.nebula.domain.favicon.repository;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface FaviconRepository extends Neo4jRepository<Favicon, String> {
    @Query("""
        MATCH (f:Favicon)
        WHERE f.faviconUrl = $faviconUrl
        return favicon
           \s""")
    Favicon findByFaviconUrl(String faviconUrl);
}
