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
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성하는 API / {name: string} 이렇게만 보내도 가능합니다. 지금 스웨거에 있는 JSON 방식은 무시해도됨")
    @PostMapping
    public ApiResponse<CreateCategoryResponseDTO> createCategory(@AuthUser User user,
        @RequestBody @Valid CreateCategoryRequestDTO request) {
        CreateCategoryResponseDTO responseDto = categoryCommandService.createCategory(request, user.getId());
        return ApiResponse.onSuccessCreated(responseDto);
    }

    // 카테고리 전체 조회 API
    @Operation(summary = "카테고리 전체 조회", description = "사용자의 모든 카테고리를 조회하는 API")
    @GetMapping
    public ApiResponse<GetCategoryListResponseDTO> getCategoryList(@AuthUser User user) {
        GetCategoryListResponseDTO responseDto = categoryQueryService.getCategoryList(user.getId());
        return ApiResponse.onSuccess(responseDto);
    }

    // 카테고리 이름 수정 API
    @Operation(summary = "카테고리 이름 수정", description = "특정 카테고리의 이름을 수정하는 API")
    @PatchMapping("{categoryId}")
    public ApiResponse<UpdateCategoryOneResponseDTO> updateCategory(@RequestBody UpdateCategoryOneRequestDTO requestDTO, @PathVariable UUID categoryId) {
        UpdateCategoryOneResponseDTO responseDTO = categoryCommandService.updateCategory(requestDTO, categoryId);
        return ApiResponse.onSuccess(responseDTO);
    }

    // 카테고리 삭제
    @Operation(summary = "카테고리 삭제", description = "특정 카테고리를 비활성화(삭제)하는 API")
    @PatchMapping("/{categoryId}/deactivate")
    public ApiResponse<DeleteCategoryResponseDTO> deleteCategory(@AuthUser User user, @PathVariable UUID categoryId){
        DeleteCategoryResponseDTO responseDTO = categoryCommandService.deleteCategory(user.getId(), categoryId);
        return ApiResponse.onSuccess(responseDTO);
    }
}

