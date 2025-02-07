package com.team_nebula.nebula.domain.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;

@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Property("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Property("updated_at")
    private LocalDateTime updatedAt;
}

