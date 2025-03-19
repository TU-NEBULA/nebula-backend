package com.team_nebula.nebula.domain.term.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.service.TermService;
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
	public ApiResponse<?> createTerm(@RequestBody TermRequestDTO request) {
		Term term = termService.createTerm(request);
		return ApiResponse.onSuccessCreated("termId: " + term.getId());
	}

	@GetMapping
	public ApiResponse<List<TermResponseDTO>> getTerms() {
		return ApiResponse.onSuccess(termService.getTerms());
	}

	@DeleteMapping("/{termId}")
	public ApiResponse<?> deleteTerm(@PathVariable Long termId) {
		termService.deleteTerm(termId);
		return ApiResponse.onSuccess("이용약관 삭제 성공");
	}
}
