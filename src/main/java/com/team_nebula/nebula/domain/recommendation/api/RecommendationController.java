package com.team_nebula.nebula.domain.recommendation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;
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
}