package com.team_nebula.nebula.domain.link.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import jakarta.persistence.GeneratedValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Node
@Getter
@NoArgsConstructor
public class Link extends BaseEntity {
    @Id
    @GeneratedValue
    private long id;

    @Property("sharedKeywordNum")
    private int sharedKeywordNum;

    @Property("similarityScore")
    private double similarityScore;

    @Property("linked_two_node_Id")
    private List<Long> linkedNode;

    @Builder
    public Link(int sharedKeywordNum, double similarityScore, List<Long> linkedNode) {
        this.sharedKeywordNum = sharedKeywordNum;
        this.similarityScore = similarityScore;
        this.linkedNode = linkedNode;
    }
}
