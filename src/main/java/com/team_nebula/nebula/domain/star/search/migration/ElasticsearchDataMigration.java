package com.team_nebula.nebula.domain.star.search.migration;

import com.team_nebula.nebula.domain.star.converter.StarConverter;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import com.team_nebula.nebula.domain.star.search.listener.StarSearchEventListener;
import com.team_nebula.nebula.domain.star.search.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.team_nebula.nebula.domain.star.converter.StarConverter.convertToSearchDocument;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "elasticsearch.migration.enabled", havingValue = "true", matchIfMissing = false)
public class ElasticsearchDataMigration {

    private final StarRepository starRepository;
    private final ElasticsearchService elasticsearchService;

    @EventListener(ApplicationReadyEvent.class)
    @Async("taskExecutor")
    public void migrateExistingData() {
        log.info("Starting Elasticsearch data migration...");

        try {
            // 기존 인덱스 삭제
            elasticsearchService.deleteIndex("star_search");

            // 모든 사용자의 스타 데이터를 DTO로 조회
            List<GetStarOneWithUserIdResponseDTO> allStars = starRepository.findAllStarWithKeywordsAndFavicons();
            log.info("Found {} stars to migrate", allStars.size());

            int successCount = 0;
            for (GetStarOneWithUserIdResponseDTO starDTO : allStars) {
                try {
                    String allContent = StarSearchEventListener.buildAllContent(starDTO);
                    StarSearchDocument document = convertToSearchDocument(starDTO, allContent);
                    elasticsearchService.indexDocument(document);
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to migrate star: {}", starDTO.getStarId(), e);
                }
            }

            log.info("Migration completed: {}/{} stars migrated successfully", successCount, allStars.size());

        } catch (Exception e) {
            log.error("Migration failed", e);
        }
    }


}


