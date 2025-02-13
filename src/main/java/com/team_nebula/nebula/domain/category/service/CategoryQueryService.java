package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;

import java.util.UUID;

public interface CategoryQueryService {
    public GetCategoryListResponseDTO getCategoryList(Long userId);

    public String findCategoryNameByStar(UUID starId);
    }
