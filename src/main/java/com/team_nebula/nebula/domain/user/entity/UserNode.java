package com.team_nebula.nebula.domain.user.entity;

import com.team_nebula.nebula.domain.category.entity.Category;
import com.team_nebula.nebula.domain.common.BaseEntity;
import com.team_nebula.nebula.domain.star.entity.Star;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Node
public class UserNode extends BaseEntity {

    @Id
    private Long userId;

    @Relationship(type = "CREATED", direction = Relationship.Direction.OUTGOING)
    private Set<Star> stars = new HashSet<>();

    @Relationship(type = "GENERATED", direction = Relationship.Direction.OUTGOING)
    private Set<Category> categorySet = new HashSet<>();


}
