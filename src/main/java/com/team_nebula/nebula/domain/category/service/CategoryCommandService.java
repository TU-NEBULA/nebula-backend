package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;

public interface CategoryCommandService {

    public CreateCategoryResponseDTO createCategory(CreateCategoryRequestDTO request);
}
