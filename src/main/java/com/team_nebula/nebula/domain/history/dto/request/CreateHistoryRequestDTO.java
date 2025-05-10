package com.team_nebula.nebula.domain.history.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHistoryRequestDTO {
	private Double lastVisitTime;
	private String title;
	private Long typedCount;
	private String url;
	private Long visitCount;
}