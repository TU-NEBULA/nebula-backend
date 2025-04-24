package com.team_nebula.nebula.domain.history.service;

import java.util.List;

import com.team_nebula.nebula.domain.history.dto.request.CreateHistoryRequestDTO;

public interface HistoryCommandService {
	void createHistory(List<CreateHistoryRequestDTO> requests, Long userId);
}