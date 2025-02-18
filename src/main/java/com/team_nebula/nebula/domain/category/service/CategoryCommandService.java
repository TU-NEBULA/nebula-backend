package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.request.UpdateCategoryOneRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.UpdateCategoryOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;

import java.util.UUID;

public interface CategoryCommandService {

    public CreateCategoryResponseDTO createCategory(CreateCategoryRequestDTO request, Long userId);

    public void linkStarToCategory(Star star, String categoryName);

    public String linkStarToCategoryAndGetName(Star star, String categoryName);

    public UpdateCategoryOneResponseDTO updateCategory(UpdateCategoryOneRequestDTO requestDTO, UUID categoryId);

    }
