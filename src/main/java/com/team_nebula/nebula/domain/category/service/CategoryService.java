package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDto;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDto;
import com.team_nebula.nebula.domain.category.entity.Category;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.domain.oauth.service.CustomOAuth2UserService;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserNodeRepository userNodeRepository;

    public CreateCategoryResponseDto createCategory(CreateCategoryRequestDto request) {

        // userName으로 임시 인증 -> 추후에 JWT 유저 인증으로 수정할 계획
        UserNode userNode = userNodeRepository.findByUsername(request.getUserName());

        // 카테고리 중복 여부 체크
        boolean categoryExists = categoryRepository.existsByName(request.getName());
        if (categoryExists) {
            throw new GeneralException(ErrorStatus._CATEGORY_ALREADY_EXIST);
        }

        // 카테고리 노드 생성
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);

        // 유저->카테고리 관계 연결
        userNode.getCategorySet().add(category);
        categoryRepository.save(category);

        return CreateCategoryResponseDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
