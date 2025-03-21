package com.team_nebula.nebula.domain.term.converter;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;

public class TermConverter {

	public static Term of(TermRequestDTO request) {
		return Term.builder()
			.name(request.getName())
			.content(request.getContent())
			.termType(request.getType())
			.build();
	}

	public static TermResponseDTO toTermResponseDTO(Term term) {
		return TermResponseDTO.builder()
			.id(term.getId())
			.name(term.getName())
			.content(term.getContent())
			.type(term.getTermType())
			.build();
	}
}
