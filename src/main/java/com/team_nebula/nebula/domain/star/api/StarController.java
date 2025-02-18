package com.team_nebula.nebula.domain.star.api;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.*;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 ]")
@RequestMapping("/api/v1/stars")
public class StarController {

    private final StarCommandService starCommandService;
    private final StarQueryService starQueryService;

    // 스타 생성 API(크롬 익스텐션에서 추가하는 경우)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CreateStarResponseDTO> createStar(
            @AuthUser User user,
            @RequestPart(value = "htmlFile",required = false) MultipartFile htmlFile,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "siteUrl") String siteUrl
    ){
        CreateStarResponseDTO responseDTO = starCommandService.createFirstStar(user, htmlFile, title, siteUrl);

        return ApiResponse.onSuccessCreated(responseDTO);
    }

    // 스타 입력 완료 API(데이터 입력 후 나머지 노드 생성)
    @PatchMapping("/complete/{starId}")
    public ApiResponse<PutStarResponseDTO> createCompleteStar(@PathVariable UUID starId, @RequestBody CreateStarRequestDTO requestDTO){
        PutStarResponseDTO responseDTO = starCommandService.createCompleteStar(starId, requestDTO);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 전체 조회 API
    @GetMapping
    public ApiResponse<GetStarListResponseDTO> getStarList(@AuthUser User user){
        GetStarListResponseDTO responseDTO = starQueryService.getStarList(user.getId());

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 단일 조회 API
    @GetMapping("/{starId}")
    public ApiResponse<GetStarOneResponseDTO> getStarOne(@PathVariable UUID starId){
        GetStarOneResponseDTO responseDTO = starQueryService.getStarOne(starId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 카테고리별 스타 조회 API
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByCategory(@AuthUser User user, @PathVariable UUID categoryId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInCategory(user.getId(), categoryId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 키워드별 스타 조회 API
    @GetMapping("/keywords/{keywordId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByKeyword(@AuthUser User user, @PathVariable String keywordId){
        System.out.println("KeywordId: " + keywordId);
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInKeyword(user.getId(), keywordId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 검색 API
    @GetMapping("/search")
    public ApiResponse<GetSearchedStarListResponseDTO> searchStar(@RequestParam String title, @AuthUser User user){
        GetSearchedStarListResponseDTO responseDTO = starQueryService.searchStars(user.getId(), title);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 수정 API
    @PatchMapping("/{starId}")
    public ApiResponse<GetStarOneResponseDTO> createCompleteStar(
            @PathVariable UUID starId,
            @RequestBody UpdateStarOneRequestDTO requestDTO
    ){
        GetStarOneResponseDTO responseDTO = starCommandService.updateStar(starId, requestDTO);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 삭제 API
    @PatchMapping("/delete/{starId}")
    public ApiResponse<DeleteStarResponseDTO> deleteStar(@AuthUser User user, @PathVariable UUID starId){
        DeleteStarResponseDTO responseDTO = starCommandService.deleteStar(starId);

        return ApiResponse.onSuccess(responseDTO);
    }
}
