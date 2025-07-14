package com.team_nebula.nebula.domain.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseDTO {
	private int currentPage;

	private int pageSize;

	private int totalCount;

	private int totalPages;

	private boolean hasNext;

	private boolean hasPrev;

	private Integer nextPage;

	private Integer prevPage;
} 