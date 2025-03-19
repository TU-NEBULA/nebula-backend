package com.team_nebula.nebula.domain.term.service;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.entity.Term;

public interface TermService {

	Term createTerm(Long userId, TermRequestDTO request);
}
