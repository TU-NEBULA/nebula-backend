package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.GetCategoryOneResponseDTO;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    @Override
    public GetCategoryListResponseDTO getCategoryList(Long userId) {

        List<Map<String, Object>> categoryData = categoryRepository.findUserCategoriesWithStarCount(userId);

        if (categoryData.isEmpty()) {
            throw new GeneralException(ErrorStatus._CATEGORY_NOT_FOUND);
        }

        List<GetCategoryOneResponseDTO> categroyList = categoryData.stream()
                .map(data -> GetCategoryOneResponseDTO.builder()
                        .id((String) data.get("id"))
                        .name((String) data.get("name"))
                        .includedStarCnt(((Number) data.get("includedStarCnt")).intValue())
                        .build())
                .toList();

        return GetCategoryListResponseDTO.builder()
                .totalCount(categroyList.size())
                .categoryList(categroyList)
                .build();
    }
}
