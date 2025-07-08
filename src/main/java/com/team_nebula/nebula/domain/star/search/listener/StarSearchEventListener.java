package com.team_nebula.nebula.domain.star.search.listener;

import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import com.team_nebula.nebula.domain.star.search.event.StarCreatedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarDeletedEvent;
import com.team_nebula.nebula.domain.star.search.event.StarUpdatedEvent;
import com.team_nebula.nebula.domain.star.search.batch.ElasticsearchBatchService;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.team_nebula.nebula.domain.star.converter.StarConverter.convertToSearchDocument;

@Component
@RequiredArgsConstructor
@Slf4j
public class StarSearchEventListener {

    private final ElasticsearchBatchService elasticsearchBatchService;
    private final ElasticsearchService elasticsearchService;

    private final Queue<StarSearchDocument> pendingDocuments = new ConcurrentLinkedQueue<>();

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

            try {
                GetStarOneWithUserIdResponseDTO starDTO = event.getStarDTO();
                String allContent = buildAllContent(starDTO);
                StarSearchDocument document = convertToSearchDocument(starDTO, allContent);
                pendingDocuments.offer(document);
                log.warn("Added failed updated document to batch queue: {}", starDTO.getStarId());
            } catch (Exception batchException) {
                log.error("Failed to add updated document to batch queue", batchException);
            }
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

            try {
                GetStarOneWithUserIdResponseDTO starDTO = event.getStarDTO();
                String allContent = buildAllContent(starDTO);
                StarSearchDocument document = convertToSearchDocument(starDTO, allContent);
                pendingDocuments.offer(document);
                log.warn("Added failed updated document to batch queue: {}", starDTO.getStarId());
            } catch (Exception batchException) {
                log.error("Failed to add updated document to batch queue", batchException);
            }
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

    @Scheduled(fixedDelay = 30000)
    public void processPendingBatch() {
        if (!pendingDocuments.isEmpty()) {
            List<StarSearchDocument> batch = new ArrayList<>();
            while (!pendingDocuments.isEmpty() && batch.size() < 100) {
                batch.add(pendingDocuments.poll());
            }
            elasticsearchBatchService.processBatchAsync(batch);
        }
    }
}
