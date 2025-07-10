package com.team_nebula.nebula.domain.recommendation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.recommendation.dto.request.SearchBasedRecommendationRequestDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.ClusterTrendsResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.SearchBasedRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.service.RecommendationService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "[추천]")
public class RecommendationController {

	private final RecommendationService recommendationService;

	@Operation(summary = "전체적인 콘텐츠 추천", description = "사용자 프로파일과 클러스터 분석을 기반으로 전체적인 콘텐츠를 추천하는 API")
	@GetMapping("/general")
	public ApiResponse<GeneralRecommendationResponseDTO> generalRecommendation(
		@AuthUser Long userId,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "true") boolean excludeViewed,
		@RequestParam(defaultValue = "true") boolean diversify,
		@RequestParam(defaultValue = "week") String timeRange) {
		return ApiResponse.onSuccess(
			recommendationService.generalRecommendation(userId, limit, category, excludeViewed, diversify, timeRange));
	}

	@Operation(summary = "검색 기반 실시간 추천", description = "사용자의 검색어를 분석하여 실시간으로 관련 콘텐츠를 추천하는 API")
	@PostMapping("/search-based")
	public ApiResponse<SearchBasedRecommendationResponseDTO> searchBasedRecommendation(
		@AuthUser Long userId,
		@RequestBody SearchBasedRecommendationRequestDTO request) {
		return ApiResponse.onSuccess(recommendationService.searchBasedRecommendation(userId, request));
	}

	@Operation(summary = "클러스터 트렌드 분석", description = "사용자 클러스터별 트렌딩 키워드와 관심사를 분석하는 API")
	@GetMapping("/cluster-trends")
	public ApiResponse<ClusterTrendsResponseDTO> clusterTrends(
		@RequestParam(required = false) Integer clusterId,
		@RequestParam(defaultValue = "week") String timePeriod,
		@RequestParam(defaultValue = "true") boolean includeGlobal) {
		return ApiResponse.onSuccess(recommendationService.clusterTrends(clusterId, timePeriod, includeGlobal));
	}
}