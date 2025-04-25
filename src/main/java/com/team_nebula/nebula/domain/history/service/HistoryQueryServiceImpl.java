package com.team_nebula.nebula.domain.history.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.history.converter.HistoryConverter;
import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.history.repository.HistoryRepository;
import com.team_nebula.nebula.domain.star.service.StarQueryService;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryQueryServiceImpl implements HistoryQueryService {

	private final HistoryRepository historyRepository;
	private final UserQueryService userQueryService;
	private final StarQueryService starQueryService;

	@Override
	public List<GetHistoryListResponseDTO> getHistories(Long userId, Pageable pageable) {
		User user = userQueryService.getUserEntity(userId);
		Page<History> historyPage = historyRepository.findAllByUser(user, pageable);
		List<History> historyList = historyPage.getContent();

		List<String> urls = historyList.stream()
			.map(History::getUrl)
			.toList();

		Set<String> starUrls = starQueryService.getStarUrls(urls, userId);

		return historyList.stream()
			.map(history -> HistoryConverter.convertToHistoryListDto(history, starUrls))
			.toList();
	}
}
