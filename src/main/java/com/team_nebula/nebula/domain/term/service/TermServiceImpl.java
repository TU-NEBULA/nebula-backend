package com.team_nebula.nebula.domain.term.service;

import org.springframework.stereotype.Service;

import com.team_nebula.nebula.domain.term.converter.TermConverter;
import com.team_nebula.nebula.domain.term.dto.request.TermRequestDTO;
import com.team_nebula.nebula.domain.term.entity.Term;

import jakarta.transaction.Transactional;

@Service
public class TermServiceImpl implements TermService {

	@Override
	@Transactional
	public Term createTerm(Long userId, TermRequestDTO request) {

		return TermConverter.of(userId, request);
	}
}
