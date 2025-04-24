package com.team_nebula.nebula.domain.history.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateHistoryRequestDTO {
	private Double lastVisitTime;
	private String title;
	private Long typedCount;
	private String url;
	private Long visitCount;
}