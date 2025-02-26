package com.team_nebula.nebula.domain.keyword.api;

import com.team_nebula.nebula.domain.keyword.service.KeywordCommandService;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "[ 키워드 ]")
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordCommandService keywordCommandService;

    // 고립된 키워드 노드 삭제 API
    @Operation(summary = "고립 키워드 삭제", description = "키워드 노드는 공유 노드이기 때문에 고립된 노드가 발생가능. 그래서 관리자가 고립된 키워드 노드를 삭제할 수 있어야함")
    @GetMapping("/cleanup")
    public ApiResponse<String> cleanupKeywords() {
        String deleteMessage = keywordCommandService.deleteKeywords();
        return ApiResponse.onSuccess(deleteMessage);
    }

}
