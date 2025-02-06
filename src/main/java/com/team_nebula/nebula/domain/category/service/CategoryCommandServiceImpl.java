package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.entity.Category;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final UserNodeRepository userNodeRepository;

    @Override
    public CreateCategoryResponseDTO createCategory(CreateCategoryRequestDTO request, Long userId) {

        UserNode userNode = userNodeRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        // 카테고리 중복 여부 체크
        boolean categoryExists = categoryRepository.existsByName(request.getName());
        if (categoryExists) {
            throw new GeneralException(ErrorStatus._CATEGORY_ALREADY_EXIST);
        }

        // 카테고리 노드 생성
        Category category = Category.builder()
                .name(request.getName())
                .build();
        categoryRepository.save(category);

        System.out.println("------------ 관계 설정 시작 -------------");
        // 유저->카테고리 관계 연결
        userNode.getCategorySet().add(category);
        System.out.println("관계 설정 여부" + userNode.getCategorySet().size());
        userNodeRepository.save(userNode);
        System.out.println("------------ 관계 설정 끝 -------------");


        return CreateCategoryResponseDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Override
    public void linkStarToCategory(Star star, String categoryName){
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new GeneralException(ErrorStatus._CATEGORY_NOT_FOUND));

        category.getStars().add(star);
        categoryRepository.save(category);
    }

}


