package com.team_nebula.nebula.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryListResponseDTO {
    private int totalCount;
    private List<GetCategoryOneResponseDTO> categoryList;
}
