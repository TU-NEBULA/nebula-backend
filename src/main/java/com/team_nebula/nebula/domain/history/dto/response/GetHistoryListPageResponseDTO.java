package com.team_nebula.nebula.domain.history.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetHistoryListPageResponseDTO {
    private int maxPage;
    private boolean hasNext;
    private List<GetHistoryListResponseDTO> content;

}
