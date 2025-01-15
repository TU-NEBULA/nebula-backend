package com.team_nebula.nebula.domain.star.entity;

import com.team_nebula.nebula.domain.common.BaseEntity;
import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.link.entity.Link;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
@Getter
@Setter
@NoArgsConstructor
public class Star extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Property("site_url")
    private String siteUrl;

    @Property("thumbnail_url")
    private String thumbnailUrl;

    @Property("summary_ai")
    private String summaryAI;

    @Lob
    private String memoUser;

    private int views;

    @Property("html_file_url")
    private String htmlFileUrl;

    private String embedding;

    @Relationship(type = "LINKED", direction = Relationship.Direction.OUTGOING)
    private Set<Link> links = new HashSet<>();

    @Relationship(type = "TAGGED", direction = Relationship.Direction.OUTGOING)
    private Set<Keyword> keywords = new HashSet<>();

    public Star(String title, String siteUrl, String thumbnailUrl, String summaryAI, String memoUser, int views,
                String htmlFileUrl, String embedding, Set<Link> links, Set<Keyword> keywords) {
        this.title = title;
        this.siteUrl = siteUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.summaryAI = summaryAI;
        this.memoUser = memoUser;
        this.views = views;
        this.htmlFileUrl = htmlFileUrl;
        this.embedding = embedding;
        this.links = links;
        this.keywords = keywords;
    }
}
