package com.team_nebula.nebula.domain.history.dto.response;

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
	private boolean isStarred;
}
