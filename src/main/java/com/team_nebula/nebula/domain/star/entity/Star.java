package com.team_nebula.nebula.domain.star.entity;

import com.team_nebula.nebula.domain.favicon.entity.Favicon;
import com.team_nebula.nebula.global.common.BaseEntity;
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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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

    @Property("siteUrl")
    private String siteUrl;

    @Property("thumbnailUrl")
    private String thumbnailUrl;

    @Property("summaryAI")
    private String summaryAI;

    @Lob
    @Property("userMemo")
    private String userMemo;

    // 조회수
    @Property("views")
    private Integer views;

    @Property("lastAccessedAt")
    private OffsetDateTime lastAccessedAt;


    @Property("html_file_url")
    private String htmlFileUrl;

    @Property("isDeletedStatus")
    private Boolean isDeletedStatus;

    @Relationship(type = "LINKED", direction = Relationship.Direction.OUTGOING)
    private Set<Link> links = new HashSet<>();

    @Relationship(type = "TAGGED", direction = Relationship.Direction.OUTGOING)
    private Set<Keyword> keywords = new HashSet<>();

    @Relationship(type = "HAS_FAVICON", direction = Relationship.Direction.OUTGOING)
    private Set<Favicon> favicons = new HashSet<>();

    @Builder
    public Star(String title, String siteUrl, String thumbnailUrl, String summaryAI, String userMemo, int views,
                OffsetDateTime lastAccessedAt, String htmlFileUrl) {

        this.id = UUID.randomUUID();
        this.title = title;
        this.siteUrl = siteUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.summaryAI = summaryAI;
        this.userMemo = userMemo;
        this.views = views;
        this.htmlFileUrl = htmlFileUrl;
        this.isDeletedStatus = false;
        this.lastAccessedAt = lastAccessedAt;
    }

    public void updateStar(String thumbnailUrl, String summaryAI, String userMemo) {
        this.thumbnailUrl = thumbnailUrl;
        this.summaryAI = summaryAI;
        this.userMemo = userMemo;
        this.isDeletedStatus = false;
    }

    public void updateTitle(String title){
        this.title = title;
    }

    public void updateSummaryAI(String summaryAI){
        this.summaryAI = summaryAI;
    }

    public void updateUserMemo(String userMemo){
        this.userMemo = userMemo;
    }

    public void updateIsDeletedStatus(){
        this.isDeletedStatus = true;
    }

    public void updateLastAccessedAt(){ this.lastAccessedAt = OffsetDateTime.now(); }

}
