package com.team_nebula.nebula.domain.history.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.history.dto.request.CreateHistoryRequestDTO;
import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.service.HistoryCommandService;
import com.team_nebula.nebula.domain.history.service.HistoryQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/histories")
public class HistoryController {

	private final HistoryCommandService historyCommandService;
	private final HistoryQueryService historyQueryService;

	// 방문기록 생성 API
	@Operation(summary = "방문기록 생성", description = "사용자의 모든 방문기록 생성하는 API")
	@PostMapping
	public ApiResponse<?> createHistories(
		@AuthUser Long userId,
		@RequestBody List<CreateHistoryRequestDTO> requests) {
		historyCommandService.createHistory(requests, userId);
		return ApiResponse.onSuccessCreated("방문기록 생성 성공");
	}

	// 방문기록 전체 조회 API
	@Operation(summary = "방문기록 전체 조회", description = "사용자의 모든 방문기록 조회하는 API")
	@GetMapping
	public ApiResponse<List<GetHistoryListResponseDTO>> getHistories(@AuthUser Long userId) {
		return ApiResponse.onSuccess(historyQueryService.getHistories(userId));
	}
}
