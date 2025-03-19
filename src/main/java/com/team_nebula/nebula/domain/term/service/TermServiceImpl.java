package com.team_nebula.nebula.domain.term.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.term.converter.TermConverter;
import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.repository.TermRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

	private final TermRepository termRepository;

	@Override
	@Transactional
	public Term createTerm(TermRequestDTO request) {

		Term term = TermConverter.of(request);

		termRepository.save(term);

		return term;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TermResponseDTO> getTerms() {
		List<Term> terms = termRepository.findAll();

		return terms.stream()
			.map(TermConverter::toTermResponseDTO)
			.toList();
	}

	@Override
	@Transactional
	public void deleteTerm(Long termId) {

		Term term = termRepository.findById(termId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TERM_NOT_FOUND));

		termRepository.delete(term);
	}
}
