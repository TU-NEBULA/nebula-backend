package com.team_nebula.nebula.domain.star.repository;

import com.team_nebula.nebula.domain.star.dto.response.GetCategoryKeywordStarRawDTO;

import java.util.List;

public interface StarNeo4jRepositoryCustom {
    List<GetCategoryKeywordStarRawDTO> fetchRawCategoryKeywordStarData(Long userId);
}
