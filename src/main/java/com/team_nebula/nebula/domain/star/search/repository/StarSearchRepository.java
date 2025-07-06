package com.team_nebula.nebula.domain.star.search.repository;

import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StarSearchRepository extends ElasticsearchRepository<StarSearchDocument, String> {

//    Page<StarSearchDocument> findByUserIdAndTitle(Long userId, String title, Pageable pageable);
//
//    Page<StarSearchDocument> findByUserIdAndAllContent(Long userId, String content, Pageable pageable);
}
