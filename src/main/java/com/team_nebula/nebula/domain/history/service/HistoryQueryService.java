package com.team_nebula.nebula.domain.history.service;

import java.util.List;

import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListPageResponseDTO;
import org.springframework.data.domain.Pageable;

import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;

public interface HistoryQueryService {
	GetHistoryListPageResponseDTO getHistories(Long userId, Pageable pageable);
}
