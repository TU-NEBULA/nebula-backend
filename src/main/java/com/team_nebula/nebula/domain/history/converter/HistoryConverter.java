package com.team_nebula.nebula.domain.history.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.entity.History;

public class HistoryConverter {

	public static List<GetHistoryListResponseDTO> convertToHistoryListDto(List<History> historyList) {
		return historyList.stream()
			.map(GetHistoryListResponseDTO::from)
			.collect(Collectors.toList());
	}
}
