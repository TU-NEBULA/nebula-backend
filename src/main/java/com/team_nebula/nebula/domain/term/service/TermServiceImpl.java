package com.team_nebula.nebula.domain.term.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.term.converter.TermConverter;
import com.team_nebula.nebula.domain.term.dto.request.AgreeTermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.dto.response.TermResponseDTO;
import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.entity.TermType;
import com.team_nebula.nebula.domain.term.entity.UserTerm;
import com.team_nebula.nebula.domain.term.repository.TermRepository;
import com.team_nebula.nebula.domain.term.repository.UserTermRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

	private final TermRepository termRepository;
	private final UserTermRepository userTermRepository;
	private final UserRepository userRepository;

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

	@Override
	@Transactional
	public void agreeTerm(Long userId, AgreeTermRequestDTO request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		for (Integer key : request.getAgreeMap().keySet()) {

			Long termId = key.longValue();
			Boolean agreed = request.getAgreeMap().get(key);

			Term term = termRepository.findById(termId)
				.orElseThrow(() -> new GeneralException(ErrorStatus._TERM_NOT_FOUND));

			if (term.getTermType().equals(TermType.MANDATORY) && !Boolean.TRUE.equals(agreed)) {
				throw new GeneralException(ErrorStatus._REQUIRED_TERM_NOT_AGREED);
			}

			UserTerm userTerm = UserTerm.builder()
				.user(user)
				.term(term)
				.agreed(request.getAgreeMap().get(key))
				.build();

			userTermRepository.save(userTerm);
		}
	}
}
