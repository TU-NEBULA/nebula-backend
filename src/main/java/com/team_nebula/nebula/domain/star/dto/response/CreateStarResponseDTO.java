package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStarResponseDTO {

    private UUID starId;
    private String title;
    private String categoryName;
    private List<String> keywordList;
}
