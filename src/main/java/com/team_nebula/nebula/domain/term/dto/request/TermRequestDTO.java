package com.team_nebula.nebula.domain.term.dto.request;

import com.team_nebula.nebula.domain.term.entity.TermType;

import lombok.Getter;

@Getter
public class TermRequestDTO {
	private String name;
	private String content;
	private TermType type;
}
