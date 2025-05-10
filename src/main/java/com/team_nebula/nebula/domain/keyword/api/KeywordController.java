package com.team_nebula.nebula.domain.keyword.api;

import com.team_nebula.nebula.domain.keyword.dto.response.GetMostUsedKeywordListResponseDTO;
import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.domain.keyword.service.KeywordQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Tag(name = "[ 키워드 ]")
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordCommandService keywordCommandService;
    private final KeywordQueryService keywordQueryService;

    // 키워드 전체조회 API
    @Operation(summary = "키워드 전체 조회", description = "사용자가 만든 키워드 전체를 조회할 수 있다.")
    @GetMapping()
    public ApiResponse<List<String>> getKeywordList(@AuthUser Long userId) {
        List<String> keywordList = keywordQueryService.getKeywords(userId);
        return ApiResponse.onSuccess(keywordList);
    }

    // 고립된 키워드 노드 삭제 API
    @Operation(summary = "고립 키워드 삭제", description = "키워드 노드는 공유 노드이기 때문에 고립된 노드가 발생가능. 그래서 관리자가 고립된 키워드 노드를 삭제할 수 있어야함")
    @GetMapping("/cleanup")
    public ApiResponse<String> cleanupKeywords() {
        String deleteMessage = keywordCommandService.deleteKeywords();
        return ApiResponse.onSuccess(deleteMessage);
    }

    // 가장 많이 사용된 키워드 10개 조회 API
    @Operation(summary = "사용순 상위 키워드 10개 조회", description = "가장 많이 사용되고 있는 상위 10개 키워드 이름리스트를 반환한다. 추가로 순위와 키워드가 사용된 횟수를 볼 수 있다.")
    @GetMapping("/top10-used")
    public ApiResponse<GetMostUsedKeywordListResponseDTO> getKeywords() {
        GetMostUsedKeywordListResponseDTO mostUsedKeywordList = keywordQueryService.getMostUsedKeywordList();
        return ApiResponse.onSuccess(mostUsedKeywordList);
    }


}
