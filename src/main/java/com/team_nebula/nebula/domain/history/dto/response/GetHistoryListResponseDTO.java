package com.team_nebula.nebula.domain.history.dto.response;

import com.team_nebula.nebula.domain.history.entity.History;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHistoryListResponseDTO {
	private Long id;
	private Double lastVisitTime;
	private String title;
	private Long typedCount;
	private String url;
	private Long visitCount;

	public static GetHistoryListResponseDTO from(History history) {
		return GetHistoryListResponseDTO.builder()
			.id(history.getId())
			.title(history.getTitle())
			.url(history.getUrl())
			.visitCount(history.getVisitCount())
			.typedCount(history.getTypedCount())
			.lastVisitTime(history.getLastVisitTime())
			.build();
	}
}
