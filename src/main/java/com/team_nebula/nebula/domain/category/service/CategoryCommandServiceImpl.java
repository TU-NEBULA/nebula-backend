package com.team_nebula.nebula.domain.category.service;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.request.UpdateCategoryOneRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.DeleteCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.UpdateCategoryOneResponseDTO;
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

import java.util.UUID;


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

        // 유저->카테고리 관계 연결
        userNode.getCategorySet().add(category);
        userNodeRepository.save(userNode);

        return CreateCategoryResponseDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Override
    public void linkStarToCategory(Star star, String categoryName, Long userId){
        Category category = categoryRepository.findByNameAndUserId(categoryName, userId)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(categoryName)
                            .build();
                    categoryRepository.save(newCategory);
                    
                    UserNode userNode = userNodeRepository.findById(userId)
                            .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
                    userNode.getCategorySet().add(newCategory);
                    userNodeRepository.save(userNode);
                    
                    return newCategory;
                });
        category.getStars().add(star);
        categoryRepository.save(category);
    }

    @Override
    public String linkStarToCategoryAndGetName(Star star, String categoryName){

        String cname = categoryRepository.findNameByStarAndRemoveRelation(star.getId(), categoryName);

        return cname;
    }

    @Override
    public UpdateCategoryOneResponseDTO updateCategory(UpdateCategoryOneRequestDTO requestDTO, UUID categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._CATEGORY_NOT_FOUND));

        // 카테고리 중복 여부 체크
        boolean categoryExists = categoryRepository.existsByName(requestDTO.getNewName());
        if (categoryExists) {
            throw new GeneralException(ErrorStatus._CATEGORY_ALREADY_EXIST);
        }

        String newName = requestDTO.getNewName();

        category.updateName(newName);
        categoryRepository.save(category);

        return UpdateCategoryOneResponseDTO.builder()
                .newName(category.getName())
                .build();
    }

    @Override
    public DeleteCategoryResponseDTO deleteCategory(Long userId, UUID categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._CATEGORY_NOT_FOUND));

        category.updateIsDeletedStatus();
        categoryRepository.save(category);

        String deleteMessage = "Category : " + category.getName() + " was deleted";
        return DeleteCategoryResponseDTO.builder()
                .categoryId(categoryId)
                .deleteStatus(deleteMessage)
                .build();
    }

}


