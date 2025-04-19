package com.team_nebula.nebula.domain.history.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.history.converter.HistoryConverter;
import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.history.repository.HistoryRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryQueryServiceImpl implements HistoryQueryService {

	private final HistoryRepository historyRepository;
	private final UserQueryService userQueryService;

	@Override
	public List<GetHistoryListResponseDTO> getHistories(Long userId) {
		User user = userQueryService.getUserEntity(userId);

		List<History> histories = historyRepository.findByUser(user);

		return HistoryConverter.convertToHistoryListDto(histories);
	}
}
