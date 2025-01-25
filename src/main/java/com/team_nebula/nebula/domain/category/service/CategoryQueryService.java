package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;

public interface CategoryQueryService {
    public GetCategoryListResponseDTO getCategoryList(Long userId);
}
