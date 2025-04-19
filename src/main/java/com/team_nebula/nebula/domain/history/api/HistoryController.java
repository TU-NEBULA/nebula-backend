package com.team_nebula.nebula.domain.history.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.service.HistoryQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/histories")
public class HistoryController {

	private final HistoryQueryService historyQueryService;

	@GetMapping
	public ApiResponse<List<GetHistoryListResponseDTO>> getHistories(@AuthUser Long userId) {
		return ApiResponse.onSuccess(historyQueryService.getHistories(userId));
	}
}
