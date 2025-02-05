package com.team_nebula.nebula.domain.star.api;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 ]")
@RequestMapping("/api/v1/stars")
public class StarController {

    private final StarCommandService starCommandService;
    private final StarQueryService starQueryService;

    // 스타 생성 API
    @PostMapping
    public ApiResponse<CreateStarResponseDTO> createStar(
            @RequestPart(value = "thumbnailImage",required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "htmlFile",required = false) MultipartFile htmlFile,
            @RequestPart(value = "star JSON data") String starJsonData
    ){
        CreateStarFileDTO requestDTO = starQueryService.starDataParsing(thumbnailImage, htmlFile, starJsonData);

        CreateStarResponseDTO responseDTO = starCommandService.createStar(requestDTO);

        return ApiResponse.onSuccessCreated(responseDTO);
    }

    // 스타 전체 조회 API
    @GetMapping
    public ApiResponse<GetStarListResponseDTO> getStarList(@AuthUser User user){
        GetStarListResponseDTO responseDTO = starQueryService.getStarList(user.getId());

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 단일 조회 API
    @GetMapping("/{starId}")
    public ApiResponse<GetStarOneResponseDTO> getStarOne(@PathVariable Long starId){
        GetStarOneResponseDTO responseDTO = starQueryService.getStarOne(starId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 카테고리별 스타 조회 API
    @GetMapping("categories/{categoryId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByCategory(@AuthUser User user, @PathVariable Long categoryId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInCategory(user.getId(), categoryId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 키워드별 스타 조회 API
    @GetMapping("/keywords/{keywordId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByKeyword(@AuthUser User user, @PathVariable Long keywordId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInKeyword(user.getId(), keywordId);

        return ApiResponse.onSuccess(responseDTO);
    }
}
