package com.team_nebula.nebula.domain.keyword.entity;

import com.team_nebula.nebula.global.common.BaseEntity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id
    private String name;

    @Builder
    public Keyword(String name) {
        this.name = name;
    }
}
