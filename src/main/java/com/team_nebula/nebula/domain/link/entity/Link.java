package com.team_nebula.nebula.domain.link.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Node
@Getter
@Setter
@NoArgsConstructor
public class Link extends BaseEntity {
    @Id
    @GeneratedValue
    private long id;

    private int sharedKeywordNum;

    private double similarityScore;

    @Property("linked_nodes_Id")
    private List<Long> linkedNode;

    public Link(int sharedKeywordNum, double similarityScore, List<Long> linkedNode) {
        this.sharedKeywordNum = sharedKeywordNum;
        this.similarityScore = similarityScore;
        this.linkedNode = linkedNode;
    }
}
