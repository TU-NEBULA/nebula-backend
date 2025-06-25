package com.team_nebula.nebula.domain.star.search.event;

import com.team_nebula.nebula.domain.star.entity.Star;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StarUpdatedEvent {
    private final Star star;
    private final Long userId;
}
