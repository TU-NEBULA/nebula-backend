package com.team_nebula.nebula.domain.term.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserTermId implements Serializable {
	private Long user;
	private Long term;
}
