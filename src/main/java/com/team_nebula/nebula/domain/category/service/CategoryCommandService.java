package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;

public interface CategoryCommandService {

    public CreateCategoryResponseDTO createCategory(CreateCategoryRequestDTO request, Long userId);

    public void linkStarToCategory(Star star, String categoryName);

    }
