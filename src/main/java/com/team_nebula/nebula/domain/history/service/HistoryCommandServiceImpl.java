package com.team_nebula.nebula.domain.history.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.history.dto.request.CreateHistoryRequestDTO;
import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.history.repository.HistoryRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.service.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HistoryCommandServiceImpl implements HistoryCommandService {

	private final HistoryRepository historyRepository;
	private final UserQueryService userQueryService;

	@Override
	public void createHistory(List<CreateHistoryRequestDTO> requests, Long userId) {

		User user = userQueryService.getUserEntity(userId);

		for (CreateHistoryRequestDTO request : requests) {

			History history = History.builder()
				.lastVisitTime(request.getLastVisitTime())
				.title(request.getTitle())
				.typedCount(request.getTypedCount())
				.url(request.getUrl())
				.visitCount(request.getVisitCount())
				.user(user)
				.build();

			log.info(history.getTitle());
			historyRepository.save(history);
		}
	}
}
