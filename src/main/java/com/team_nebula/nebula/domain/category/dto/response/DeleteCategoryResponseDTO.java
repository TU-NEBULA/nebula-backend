package com.team_nebula.nebula.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCategoryResponseDTO {
    private UUID categoryId;
    private String deleteStatus;
}
