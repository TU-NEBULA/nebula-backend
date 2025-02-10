package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.GetCategoryOneResponseDTO;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    @Override
    public GetCategoryListResponseDTO getCategoryList(Long userId) {

        List<GetCategoryOneResponseDTO> categoryList = categoryRepository.findUserCategoriesWithStarCount(userId);

        return GetCategoryListResponseDTO.builder()
                .totalCount(categoryList.size())
                .categoryList(categoryList)
                .build();
    }
}
