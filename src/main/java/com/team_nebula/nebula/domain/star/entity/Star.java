package com.team_nebula.nebula.domain.star.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.link.entity.Link;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node
@Getter
@NoArgsConstructor
public class Star extends BaseEntity {

    @Id
    private UUID id;

    private String title;

    @Property("site_url")
    private String siteUrl;

    @Property("thumbnail_url")
    private String thumbnailUrl;

    @Property("summary_ai")
    private String summaryAI;

    @Lob
    @Property("userMemo")
    private String userMemo;

    // 조회수
    @Property("views")
    private Integer views;

    @Property("html_file_url")
    private String htmlFileUrl;

    private String embedding;

    @Relationship(type = "LINKED", direction = Relationship.Direction.OUTGOING)
    private Set<Link> links = new HashSet<>();

    @Relationship(type = "TAGGED", direction = Relationship.Direction.OUTGOING)
    private Set<Keyword> keywords = new HashSet<>();

    @Builder
    public Star(String title, String siteUrl, String thumbnailUrl, String summaryAI, String userMemo, String memoUser, int views,
                String htmlFileUrl, String embedding) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.siteUrl = siteUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.summaryAI = summaryAI;
        this.userMemo = userMemo;
        this.userMemo = memoUser;
        this.views = views;
        this.htmlFileUrl = htmlFileUrl;
        this.embedding = embedding;
    }
}
