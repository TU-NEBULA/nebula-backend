package com.team_nebula.nebula.domain.history.converter;

import java.util.Set;

import com.team_nebula.nebula.domain.history.dto.response.GetHistoryListResponseDTO;
import com.team_nebula.nebula.domain.history.entity.History;

public class HistoryConverter {

	public static GetHistoryListResponseDTO convertToHistoryListDto(History history, Set<String> bookmarkedUrls) {
		return GetHistoryListResponseDTO.builder()
			.id(history.getId())
			.lastVisitTime(history.getLastVisitTime())
			.title(history.getTitle())
			.typedCount(history.getTypedCount())
			.url(history.getUrl())
			.visitCount(history.getVisitCount())
			.isStarred(bookmarkedUrls.contains(history.getUrl()))
			.build();
	}
}