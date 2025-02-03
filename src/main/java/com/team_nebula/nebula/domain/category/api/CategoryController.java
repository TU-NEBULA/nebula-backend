package com.team_nebula.nebula.domain.category.api;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDTO;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDTO;
import com.team_nebula.nebula.domain.category.dto.response.GetCategoryListResponseDTO;
import com.team_nebula.nebula.domain.category.service.CategoryCommandService;
import com.team_nebula.nebula.domain.category.service.CategoryQueryService;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "[카테고리]")
@RequestMapping("/api/v1/bookmarks/categories")
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    // 카데고리 생성 API
    @PostMapping("")
    public ApiResponse<CreateCategoryResponseDTO> createCategory(CreateCategoryRequestDTO request) {
        CreateCategoryResponseDTO responseDto = categoryCommandService.createCategory(request);
        return ApiResponse.onSuccessCreated(responseDto);
    }

    // 카테고리 전체 조회 API
    @GetMapping("/{userId}")
    public ApiResponse<GetCategoryListResponseDTO> getCategoryList(@PathVariable Long userId) {
        GetCategoryListResponseDTO responseDto = categoryQueryService.getCategoryList(userId);
        return ApiResponse.onSuccess(responseDto);
    }
}

