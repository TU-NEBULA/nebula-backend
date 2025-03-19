package com.team_nebula.nebula.domain.term.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.service.TermService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[ 이용약관 ]")
@RequestMapping("/api/v1/terms")
public class TermController {

	private final TermService termService;

	@PostMapping
	public ApiResponse<?> createTerm(@AuthUser Long userId, @RequestBody TermRequestDTO request) {
		Term term = termService.createTerm(userId, request);
		return ApiResponse.onSuccessCreated("termId: " + term.getId());
	}
}