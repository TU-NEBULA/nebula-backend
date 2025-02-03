package com.team_nebula.nebula.domain.category.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import com.team_nebula.nebula.domain.star.entity.Star;
import jakarta.persistence.GeneratedValue;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private Set<Star> stars = new HashSet<>();

    @Builder
    public Category(String name) {
        this.name = name;
    }
}
