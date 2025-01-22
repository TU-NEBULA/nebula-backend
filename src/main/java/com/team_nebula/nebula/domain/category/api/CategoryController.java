package com.team_nebula.nebula.domain.category.api;

import com.team_nebula.nebula.domain.category.dto.request.CreateCategoryRequestDto;
import com.team_nebula.nebula.domain.category.dto.response.CreateCategoryResponseDto;
import com.team_nebula.nebula.domain.category.service.CategoryService;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "[카테고리]")
@RequestMapping("/api/v1/bookmarks/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 카데고리 생성 API
    @PostMapping("/")
    public ApiResponse<CreateCategoryResponseDto> createCategory(CreateCategoryRequestDto request) {
        CreateCategoryResponseDto responseDto = categoryService.createCategory(request);
        return ApiResponse.onSuccessCreated(responseDto);
    }
}
