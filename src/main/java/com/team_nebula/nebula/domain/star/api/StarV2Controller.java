package com.team_nebula.nebula.domain.star.api;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.AddBookMarkResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 V2 ]")
@RequestMapping("/api/v2/stars")
public class StarV2Controller {

    private final StarCommandService starCommandService;

    // 북마크 추가 API(크롬 익스텐션에서 처음 북마크를 추가하는 경우)
    @Operation(summary = "북마크 추가", description = "크롬 익스텐션에서 처음 북마크를 추가하는 API로 AI 추천 키워드 리스를 반환한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AddBookMarkResponseDTO> addBookMark(
            @AuthUser Long userId,
            @RequestPart(value = "htmlFile",required = false) MultipartFile htmlFile,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "siteUrl") String siteUrl
    ){
        AddBookMarkResponseDTO responseDTO = starCommandService.addBookMark(userId, htmlFile, title, siteUrl);

        return ApiResponse.onSuccessCreated(responseDTO);
    }

    // 스타 저장 API(데이터 입력 후 나머지 노드 생성)
    @Operation(summary = "스타 저장", description = "스타의 타이틀, 사이트 url, Tjaspdlf카테고리, 사용자메모, AI요약, 키워드를 입력하고 스타 정보 입력을 완료하는 API")
    @PatchMapping("/complete/{starId}")
    public ApiResponse<PutStarResponseDTO> createCompleteStar(
            @AuthUser Long userId,
            @PathVariable UUID starId,
            @RequestBody CreateStarRequestDTO requestDTO){
        PutStarResponseDTO responseDTO = starCommandService.createCompleteStar(userId, starId, requestDTO);

        return ApiResponse.onSuccess(responseDTO);
    }
}
