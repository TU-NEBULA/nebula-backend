package com.team_nebula.nebula.domain.star.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultResponseDTO {
    private List<SearchStarResponseDTO> stars;
    private long totalCount;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
}
