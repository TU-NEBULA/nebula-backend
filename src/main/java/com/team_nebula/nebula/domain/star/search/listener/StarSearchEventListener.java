package com.team_nebula.nebula.domain.star.search.listener;

import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import com.team_nebula.nebula.domain.star.search.event.StarCreatedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarDeletedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarUpdatedEvent;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
            GetStarOneWithUserIdResponseDTO starDTO = event.getStarDTO();
            String allContent = buildAllContent(starDTO);
            StarSearchDocument document = convertToSearchDocument(starDTO, allContent);
            elasticsearchService.indexDocument(document);
            log.info("Successfully synced created star to Elasticsearch: {}", starDTO.getStarId());
        } catch (Exception e) {
            log.error("Failed to sync created star to Elasticsearch", e);
        }
    }

    @Async
    @EventListener
    public void handleStarUpdated(StarUpdatedEvent event) {
        try {
            GetStarOneWithUserIdResponseDTO starDTO = event.getStarDTO();
            String allContent = buildAllContent(starDTO);
            StarSearchDocument document = convertToSearchDocument(starDTO, allContent);
            elasticsearchService.indexDocument(document);
            log.info("Successfully synced updated star to Elasticsearch: {}", starDTO.getStarId());
        } catch (Exception e) {
            log.error("Failed to sync updated star to Elasticsearch", e);
        }
    }

    @Async
    @EventListener
    public void handleStarDeleted(StarDeletedEvent event) {
        try {
            elasticsearchService.deleteDocument(event.getStarId());
            log.info("Successfully deleted star from Elasticsearch: {}", event.getStarId());
        } catch (Exception e) {
            log.error("Failed to delete star from Elasticsearch", e);
        }
    }

    public static String buildAllContent(GetStarOneWithUserIdResponseDTO starDTO) {
        StringBuilder content = new StringBuilder();

        if (starDTO.getTitle() != null) {
            content.append(starDTO.getTitle()).append(" ");
        }
        if (starDTO.getSummaryAI() != null) {
            content.append(starDTO.getSummaryAI()).append(" ");
        }
        if (starDTO.getUserMemo() != null) {
            content.append(starDTO.getUserMemo()).append(" ");
        }
        if (starDTO.getCategoryName() != null) {
            content.append(starDTO.getCategoryName()).append(" ");
        }
        if (starDTO.getKeywordList() != null) {
            for (String keyword : starDTO.getKeywordList()) {
                if (keyword != null) {
                    content.append(keyword).append(" ");
                }
            }
        }

        return content.toString().trim();
    }
}
