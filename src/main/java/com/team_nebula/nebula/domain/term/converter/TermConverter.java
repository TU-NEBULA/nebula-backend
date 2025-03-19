package com.team_nebula.nebula.domain.term.converter;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.entity.Term;

public class TermConverter {

	public static Term of(Long userId, TermRequestDTO request) {
		return Term.builder()
			.name(request.getName())
			.content(request.getContent())
			.termType(request.getType())
			.build();
	}
}
