package com.team_nebula.nebula.domain.favicon.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node
@Getter
@NoArgsConstructor
public class Favicon {
    @Id
    private String domain;

    @Property("faviconUrl")
    private String faviconUrl;

    @Builder
    public Favicon(String domain, String faviconUrl) {
        this.domain = domain;
        this.faviconUrl = faviconUrl;
    }
}

