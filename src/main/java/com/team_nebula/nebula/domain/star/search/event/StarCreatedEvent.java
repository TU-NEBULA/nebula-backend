package com.team_nebula.nebula.domain.star.search.event;

import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StarCreatedEvent {
    private GetStarOneWithUserIdResponseDTO starDTO;
}
