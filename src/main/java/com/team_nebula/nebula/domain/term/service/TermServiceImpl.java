package com.team_nebula.nebula.domain.term.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.term.converter.TermConverter;
import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.repository.TermRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

	private final TermRepository termRepository;

	@Override
	@Transactional
	public Term createTerm(TermRequestDTO request) {

		return TermConverter.of(request);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TermResponseDTO> getTerms() {
		List<Term> terms = termRepository.findAll();

		return terms.stream()
			.map(TermConverter::toTermResponseDTO)
			.toList();
	}
}
