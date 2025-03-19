package com.team_nebula.nebula.domain.term.dto.response;

import com.team_nebula.nebula.domain.term.entity.TermType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermResponseDTO {
	private Long id;
	private String name;
	private String content;
	private TermType type;
}
