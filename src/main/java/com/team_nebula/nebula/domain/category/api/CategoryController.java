package com.team_nebula.nebula.domain.category.api;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.request.UpdateCategoryOneRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.DeleteCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.UpdateCategoryOneResponseDTO;
import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.category.service.CategoryQueryService;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "[카테고리]")
@RequestMapping("/api/v1/stars/categories")
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    // 카데고리 생성 API
    @PostMapping
    public ApiResponse<CreateCategoryResponseDTO> createCategory(@AuthUser User user,
        @RequestBody @Valid CreateCategoryRequestDTO request) {
        CreateCategoryResponseDTO responseDto = categoryCommandService.createCategory(request, user.getId());
        return ApiResponse.onSuccessCreated(responseDto);
    }

    // 카테고리 전체 조회 API
    @GetMapping
    public ApiResponse<GetCategoryListResponseDTO> getCategoryList(@AuthUser User user) {
        GetCategoryListResponseDTO responseDto = categoryQueryService.getCategoryList(user.getId());
        return ApiResponse.onSuccess(responseDto);
    }

    // 카테고리 이름 수정 API
    @PatchMapping("{categoryId}")
    public ApiResponse<UpdateCategoryOneResponseDTO> updateCategory(@RequestBody UpdateCategoryOneRequestDTO requestDTO, @PathVariable UUID categoryId) {
        UpdateCategoryOneResponseDTO responseDTO = categoryCommandService.updateCategory(requestDTO, categoryId);
        return ApiResponse.onSuccess(responseDTO);
    }

    @PatchMapping("/{categoryId}/deactivate")
    public ApiResponse<DeleteCategoryResponseDTO> deleteCategory(@AuthUser User user, @PathVariable UUID categoryId){
        DeleteCategoryResponseDTO responseDTO = categoryCommandService.deleteCategory(user.getId(), categoryId);
        return ApiResponse.onSuccess(responseDTO);
    }
}

