package com.team_nebula.nebula.domain.star.api;

import com.team_nebula.nebula.domain.star.dto.request.CompleteStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.AddBookMarkResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetCategoryAndKeywordListDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.star.search.dto.response.SearchResultResponseDTO;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import com.team_nebula.nebula.domain.star.service.StarCommandService;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 스타 V2 ]")
@RequestMapping("/api/v2/stars")
public class StarV2Controller {

    private final StarCommandService starCommandService;
    private final StarQueryService starQueryService;

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

        return ApiResponse.onSuccess(responseDTO);
    }

    // 스타 저장 API(데이터 입력 후 나머지 노드 생성)
    @Operation(summary = "스타 저장", description = "스타의 타이틀, 사이트 url, 썸네일url, 파비콘url, 카테고리, 사용자메모, AI요약, 키워드를 입력하고 스타 정보 입력을 완료하는 API")
    @PostMapping("/save")
    public ApiResponse<CreateStarResponseDTO> createStar(
            @AuthUser Long userId,
            @RequestBody CreateStarRequestDTO requestDTO){
        CreateStarResponseDTO responseDTO = starCommandService.createStar(userId, requestDTO);

        return ApiResponse.onSuccessCreated(responseDTO);
    }

    // 스타(카테고리 -> 키워드 -> 스타) 전체 조회 API
    @Operation(summary = "스타 2D 그래프뷰 조회", description = "카테고리 - 키워드 - 스타 순서대로 데이터를 조회하는 2D 그래브뷰 API")
    @GetMapping("/2D")
    public ApiResponse<List<GetCategoryAndKeywordListDTO>> get2DStarList(@AuthUser Long userId) {
        List<GetCategoryAndKeywordListDTO> result = starQueryService.getCategoryAndKeywordList(userId);

        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "스타 검색", description = "제목, 키워드, AI요약, 메모를 통합 검색")
    @GetMapping("/search")
    public ApiResponse<SearchResultResponseDTO> searchStars(
            @AuthUser Long userId,
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SearchResultResponseDTO result = starQueryService.searchStarsV2(query, userId, page, size);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "자동완성", description = "검색어 자동완성 제안")
    @GetMapping("/search/autocomplete")
    public ApiResponse<List<String>> getAutoComplete(
            @AuthUser Long userId,
            @RequestParam("q") String query
    ) {
        List<String> suggestions = starQueryService.getAutoComplete(query, userId, 5);
        return ApiResponse.onSuccess(suggestions);
    }

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/test/cache-content-detail")
    public Map<String, Object> getCacheContentDetail(@RequestParam String query) {
        Map<String, Object> result = new HashMap<>();

        Cache cache = cacheManager.getCache("autocomplete_service");
        if (cache != null) {
            String key = query + "_1_5";
            Cache.ValueWrapper wrapper = cache.get(key);

            result.put("cacheExists", cache != null);
            result.put("keyExists", wrapper != null);
            result.put("cacheType", cache.getClass().getName());

            if (wrapper != null) {
                result.put("cachedValue", wrapper.get());
            }
        }

        return result;
    }



}
