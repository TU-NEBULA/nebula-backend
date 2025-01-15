package com.team_nebula.nebula.domain.keyword.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Keyword extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
