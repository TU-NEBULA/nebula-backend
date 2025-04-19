package com.team_nebula.nebula.domain.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.user.dto.response.UserResponseDTO;
import com.team_nebula.nebula.domain.user.service.UserQueryService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 유저 ]")
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserQueryService userQueryService;

	// 유저 조회 API
	@Operation(summary = "유저 조회", description = "유저 정보를 조회하는 API")
	@GetMapping
	public ApiResponse<UserResponseDTO> getUser(@AuthUser Long userId) {
		return ApiResponse.onSuccess(userQueryService.getUser(userId));
	}
}
