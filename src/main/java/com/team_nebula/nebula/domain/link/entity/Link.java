package com.team_nebula.nebula.domain.link.entity;

import com.team_nebula.nebula.global.common.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;
import java.util.UUID;

@Node
@Getter
@NoArgsConstructor
public class Link extends BaseEntity {
    @Id
    private UUID id;

    @Property("sharedKeywordNum")
    private int sharedKeywordNum;

    @Property("sharedKeywords")
    private List<String> sharedKeywords;

    @Property("similarityScore")
    private double similarityScore;

    @Property("linked_two_node_Id")
    private List<UUID> linkedNode;

    @Builder
    public Link(int sharedKeywordNum, double similarityScore, List<UUID> linkedNode, List<String> sharedKeywords) {
        this.id = UUID.randomUUID();
        this.sharedKeywordNum = sharedKeywordNum;
        this.similarityScore = similarityScore;
        this.linkedNode = linkedNode;
        this.sharedKeywords = sharedKeywords;
    }
}
