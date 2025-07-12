package com.team_nebula.nebula.domain.star.search.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StarDeletedEvent {
    private final String starId;
    private final Long userId;
}
