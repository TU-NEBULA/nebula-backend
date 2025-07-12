package com.team_nebula.nebula.domain.star.search.repository;

import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import org.springframework.stereotype.Repository;

@Repository
public interface StarSearchRepository {
    void save(StarSearchDocument document);
    void deleteById(String id);
    StarSearchDocument findById(String id);
}
