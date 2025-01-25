package com.team_nebula.nebula.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryResponseDTO {
    private Long categoryId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
