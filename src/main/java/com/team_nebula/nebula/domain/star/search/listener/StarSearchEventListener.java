package com.team_nebula.nebula.domain.star.search.listener;

import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.event.StarCreatedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarDeletedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarUpdatedEvent;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static com.team_nebula.nebula.domain.star.converter.StarConverter.convertToSearchDocument;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSearchEventListener {

    private final ElasticsearchService elasticsearchService;

    @Async
    @EventListener
    public void handleStarCreated(StarCreatedEvent event) {
        try {
            String allContent = buildAllContent(event.getStar());
            StarSearchDocument document = convertToSearchDocument(event.getStar(), event.getUserId(), allContent);
            elasticsearchService.indexDocument(document);
        } catch (Exception e) {
            log.error("Failed to sync created star to Elasticsearch", e);
        }
    }

    @Async
    @EventListener
    public void handleStarUpdated(StarUpdatedEvent event) {
        try {
            String allContent = buildAllContent(event.getStar());
            StarSearchDocument document = convertToSearchDocument(event.getStar(), event.getUserId(), allContent);
            elasticsearchService.indexDocument(document);
        } catch (Exception e) {
            log.error("Failed to sync updated star to Elasticsearch", e);
        }
    }

    @Async
    @EventListener
    public void handleStarDeleted(StarDeletedEvent event) {
        try {
            elasticsearchService.deleteDocument(event.getStarId());
        } catch (Exception e) {
            log.error("Failed to delete star from Elasticsearch", e);
        }
    }


    private String buildAllContent(Star star) {
        StringBuilder content = new StringBuilder();

        if (star.getTitle() != null) {
            content.append(star.getTitle()).append(" ");
        }
        if (star.getSummaryAI() != null) {
            content.append(star.getSummaryAI()).append(" ");
        }
        if (star.getUserMemo() != null) {
            content.append(star.getUserMemo()).append(" ");
        }

        star.getKeywords().forEach(keyword ->
                content.append(keyword.getName()).append(" ")
        );

        return content.toString().trim();
    }
}
