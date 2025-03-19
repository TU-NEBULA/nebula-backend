package com.team_nebula.nebula.domain.term.service;

import java.util.List;

import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;

public interface TermService {

	Term createTerm(TermRequestDTO request);

	List<TermResponseDTO> getTerms();

	void deleteTerm(Long termId);
}
