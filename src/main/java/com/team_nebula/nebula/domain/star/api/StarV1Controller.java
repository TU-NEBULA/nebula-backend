package com.team_nebula.nebula.domain.star.api;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.DeleteStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 V1 ]")
@RequestMapping("/api/v1/stars")
public class StarV1Controller {

    private final StarCommandService starCommandService;
    private final StarQueryService starQueryService;

    // 스타 생성 API(크롬 익스텐션에서 추가하는 경우)
    @Operation(summary = "스타 생성", description = "크롬 익스텐션에서 북마크(스타)를 생성하는 API / 현재 스타가 생성되면 중복된 키워드 기준으로 링크 생성여부가 결정됨, 스타간 유사도는 아직 노력중")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CreateStarResponseDTO> createStar(
            @AuthUser Long userId,
            @RequestPart(value = "htmlFile",required = false) MultipartFile htmlFile,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "siteUrl") String siteUrl
    ){
        CreateStarResponseDTO responseDTO = starCommandService.createFirstStar(userId, htmlFile, title, siteUrl);

        return ApiResponse.onSuccessCreated(responseDTO);
    }

    // 스타 입력 완료 API(데이터 입력 후 나머지 노드 생성)
    @Operation(summary = "스타 입력 완료", description = "스타의 카테고리, 사용자메모, AI요약, 키워드를 입력하고 스타 정보 입력을 완료하는 API")
    @PatchMapping("/complete/{starId}")
    public ApiResponse<PutStarResponseDTO> createCompleteStar(
            @AuthUser Long userId,
            @PathVariable UUID starId,
            @RequestBody CreateStarRequestDTO requestDTO){
        PutStarResponseDTO responseDTO = starCommandService.createCompleteStar(userId, starId, requestDTO);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 전체 조회 API
    @Operation(summary = "스타 전체 조회", description = "사용자의 모든 스타를 조회하는 API")
    @GetMapping
    public ApiResponse<GetStarListResponseDTO> getStarList(@AuthUser Long userId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarList(userId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 단일 조회 API
    @Operation(summary = "스타 단일 조회", description = "특정 스타 정보를 조회하는 API로 이 API를 실행하면 views(조회수)가 1증가함")
    @GetMapping("/{starId}")
    public ApiResponse<GetStarOneResponseDTO> getStarOne(@PathVariable UUID starId){
        GetStarOneResponseDTO responseDTO = starQueryService.getStarOne(starId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 카테고리별 스타 조회 API
    @Operation(summary = "카테고리별 스타 조회", description = "특정 카테고리에 속한 스타들을 조회하는 API")
    @GetMapping("/categories/{categoryId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByCategory(
            @AuthUser Long userId,
            @PathVariable UUID categoryId){
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInCategory(userId, categoryId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 키워드별 스타 조회 API
    @Operation(summary = "키워드별 스타 조회", description = "특정 키워드가 포함된 스타들을 조회하는 API")
    @GetMapping("/keywords/{keywordId}")
    public ApiResponse<GetStarListResponseDTO> getStarListByKeyword(
            @AuthUser Long userId,
            @PathVariable String keywordId){
        System.out.println("KeywordId: " + keywordId);
        GetStarListResponseDTO responseDTO = starQueryService.getStarListInKeyword(userId, keywordId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 검색 API
    @Operation(summary = "스타 검색", description = "title(제목)을 기준으로 스타를 검색하는 API")
    @GetMapping("/search")
    public ApiResponse<GetSearchedStarListResponseDTO> searchStar(
            @RequestParam String title,
            @AuthUser Long userId){
        GetSearchedStarListResponseDTO responseDTO = starQueryService.searchStars(userId, title);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 수정 API
    @Operation(summary = "스타 정보 수정", description = "특정 스타 정보를 수정하는 API / title(제목), categoryName(카테고리), summaryAI(AI요약), userMemo(사용자메모)를 각각 수정할 수 있음")
    @PatchMapping("/{starId}")
    public ApiResponse<GetStarOneResponseDTO> createCompleteStar(
            @AuthUser Long userId,
            @PathVariable UUID starId,
            @RequestBody UpdateStarOneRequestDTO requestDTO
    ){
        GetStarOneResponseDTO responseDTO = starCommandService.updateStar(userId, starId, requestDTO);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 삭제 API
    @Operation(summary = "스타 삭제", description = "스타를 비활성화하는 API/ 완전 삭제가 아닌 Soft Delete하는 것")
    @PatchMapping("/{starId}/deactivate")
    public ApiResponse<DeleteStarResponseDTO> deleteStar(
            @AuthUser Long userId,
            @PathVariable UUID starId){
        DeleteStarResponseDTO responseDTO = starCommandService.deleteStar(userId, starId);

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타화 취소 API
    @Operation(summary = "스타화 취소", description = "스타 등록을 취소하는 API / 크롬 익스텐션에서 북마크 추가하기를 누르고 넘어가는 화면에서 저장 말고 취소를 눌렀을때 스타를 완전 삭제하는 기능")
    @DeleteMapping("/{starId}/cancel")
    public ApiResponse<DeleteStarResponseDTO> cancelStar(
            @PathVariable UUID starId,
            @AuthUser Long userId){
        DeleteStarResponseDTO responseDTO = starCommandService.cancelStar(starId);

        return ApiResponse.onSuccess(responseDTO);
    }
}
