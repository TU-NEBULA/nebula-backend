package com.team_nebula.nebula.domain.star.search.migration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.elasticsearch.indices.update_aliases.AddAction;
import co.elastic.clients.elasticsearch.indices.update_aliases.RemoveAction;
import com.team_nebula.nebula.domain.star.converter.StarConverter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.team_nebula.nebula.domain.star.converter.StarConverter.convertToSearchDocument;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "elasticsearch.migration.enabled", havingValue = "true", matchIfMissing = false)
public class ElasticsearchDataMigration {

    private final StarRepository starRepository;
    private final ElasticsearchClient elasticsearchClient;

    private static final String INDEX_ALIAS = "star_search";
    private static final String INDEX_PATTERN = "star_search_v";

    @EventListener(ApplicationReadyEvent.class)
    @Async("taskExecutor")
    public void migrateExistingData() {
        log.info("Starting safe Elasticsearch data migration...");

        try {
            // 1. 새로운 인덱스 이름 생성
            String newIndexName = generateNewIndexName();
            log.info("Creating new index: {}", newIndexName);

            // 2. 새 인덱스 생성
            createNewIndex(newIndexName);

            // 3. 데이터 마이그레이션
            int migratedCount = migrateDataToNewIndex(newIndexName);

            // 4. 마이그레이션 검증
            if (verifyMigration(newIndexName, migratedCount)) {
                // 5. Alias 원자적 전환
                switchAliasToNewIndex(newIndexName);

                // 6. 구 인덱스 정리
                cleanupOldIndices(newIndexName);

                log.info("Migration completed successfully: {} stars migrated to {}",
                        migratedCount, newIndexName);
            } else {
                // 마이그레이션 실패 시 롤백
                rollbackMigration(newIndexName);
            }

        } catch (Exception e) {
            log.error("Migration failed", e);
        }
    }

    /**
     * 타임스탬프 기반 새 인덱스 이름 생성
     */
    private String generateNewIndexName() {
        return INDEX_PATTERN + System.currentTimeMillis();
    }

    /**
     * 새 인덱스 생성
     */
    private void createNewIndex(String indexName) throws Exception {
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("0")
                )
        );

        elasticsearchClient.indices().create(request);
        log.info("Successfully created new index: {}", indexName);
    }

    /**
     * 데이터를 새 인덱스로 마이그레이션
     */
    private int migrateDataToNewIndex(String newIndexName) {
        List<GetStarOneWithUserIdResponseDTO> allStars = starRepository.findAllStarWithKeywordsAndFavicons();
        log.info("Found {} stars to migrate", allStars.size());

        int successCount = 0;
        for (GetStarOneWithUserIdResponseDTO starDTO : allStars) {
            try {
                String allContent = StarSearchEventListener.buildAllContent(starDTO);
                StarSearchDocument document = convertToSearchDocument(starDTO, allContent);

                // 새 인덱스로 직접 인덱싱
                indexDocumentToSpecificIndex(document, newIndexName);
                successCount++;

                if (successCount % 100 == 0) {
                    log.info("Migrated {} / {} stars", successCount, allStars.size());
                }

            } catch (Exception e) {
                log.error("Failed to migrate star: {}", starDTO.getStarId(), e);
            }
        }

        // 새 인덱스 refresh
        refreshIndex(newIndexName);
        return successCount;
    }

    /**
     * 특정 인덱스에 문서 인덱싱
     */
    private void indexDocumentToSpecificIndex(StarSearchDocument document, String indexName) throws Exception {
        IndexRequest<StarSearchDocument> request = IndexRequest.of(i -> i
                .index(indexName)
                .id(document.getId())
                .document(document)
        );

        elasticsearchClient.index(request);
    }

    /**
     * 인덱스 refresh
     */
    private void refreshIndex(String indexName) {
        try {
            RefreshRequest request = RefreshRequest.of(r -> r.index(indexName));
            elasticsearchClient.indices().refresh(request);
            log.info("Refreshed index: {}", indexName);
        } catch (Exception e) {
            log.error("Failed to refresh index: {}", indexName, e);
        }
    }

    /**
     * 마이그레이션 검증
     */
    private boolean verifyMigration(String newIndexName, int expectedCount) {
        try {
            // 새 인덱스의 문서 수 확인
            CountRequest countRequest = CountRequest.of(c -> c.index(newIndexName));
            CountResponse countResponse = elasticsearchClient.count(countRequest);

            long actualCount = countResponse.count();
            log.info("Verification: Expected {}, Actual {} documents in new index",
                    expectedCount, actualCount);

            if (actualCount != expectedCount) {
                log.error("Migration verification failed: document count mismatch");
                return false;
            }

            // 샘플 검색 테스트
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(newIndexName)
                    .size(1)
            );

            SearchResponse<StarSearchDocument> searchResponse =
                    elasticsearchClient.search(searchRequest, StarSearchDocument.class);

            if (searchResponse.hits().hits().isEmpty() && expectedCount > 0) {
                log.error("Migration verification failed: no searchable documents");
                return false;
            }

            log.info("Migration verification passed");
            return true;

        } catch (Exception e) {
            log.error("Migration verification failed with exception", e);
            return false;
        }
    }

    /**
     * Alias를 새 인덱스로 원자적 전환
     */
    private void switchAliasToNewIndex(String newIndexName) throws Exception {
        List<Action> actions = new ArrayList<>();

        try {
            // 기존 alias 조회 및 제거 액션 추가
            GetAliasRequest getAliasRequest = GetAliasRequest.of(g -> g.name(INDEX_ALIAS));
            GetAliasResponse aliasResponse = elasticsearchClient.indices().getAlias(getAliasRequest);

            for (String indexName : aliasResponse.result().keySet()) {
                actions.add(Action.of(a -> a
                        .remove(RemoveAction.of(r -> r.index(indexName).alias(INDEX_ALIAS)))
                ));
            }
        } catch (ElasticsearchException e) {
            log.info("No existing alias found, creating new one");
        }

        // 새 인덱스에 alias 추가
        actions.add(Action.of(a -> a
                .add(AddAction.of(add -> add.index(newIndexName).alias(INDEX_ALIAS)))
        ));

        // 원자적 alias 전환 실행
        UpdateAliasesRequest updateRequest = UpdateAliasesRequest.of(u -> u.actions(actions));
        elasticsearchClient.indices().updateAliases(updateRequest);

        log.info("Successfully switched alias '{}' to new index '{}'", INDEX_ALIAS, newIndexName);
    }

    /**
     * 구 인덱스들 정리
     */
    private void cleanupOldIndices(String currentIndexName) {
        try {
            GetIndexRequest getIndexRequest = GetIndexRequest.of(g -> g.index(INDEX_PATTERN + "*"));
            GetIndexResponse indexResponse = elasticsearchClient.indices().get(getIndexRequest);

            for (String indexName : indexResponse.result().keySet()) {
                if (!indexName.equals(currentIndexName)) {
                    if (!isIndexLinkedToAlias(indexName)) {
                        deleteOldIndexWithDelay(indexName);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup old indices", e);
        }
    }

    /**
     * 인덱스가 alias와 연결되어 있는지 확인
     */
    private boolean isIndexLinkedToAlias(String indexName) {
        try {
            GetAliasRequest request = GetAliasRequest.of(g -> g.index(indexName));
            GetAliasResponse response = elasticsearchClient.indices().getAlias(request);
            return !response.result().get(indexName).aliases().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 지연 후 구 인덱스 삭제
     */
    private void deleteOldIndexWithDelay(String indexName) {
        CompletableFuture.delayedExecutor(5, TimeUnit.MINUTES).execute(() -> {
            try {
                DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(indexName));
                elasticsearchClient.indices().delete(deleteRequest);
                log.info("Deleted old index: {}", indexName);
            } catch (Exception e) {
                log.warn("Failed to delete old index: {}", indexName, e);
            }
        });
    }

    /**
     * 마이그레이션 롤백
     */
    private void rollbackMigration(String newIndexName) {
        try {
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(newIndexName));
            elasticsearchClient.indices().delete(deleteRequest);
            log.info("Rolled back migration: deleted failed index {}", newIndexName);
        } catch (Exception e) {
            log.error("Failed to rollback migration", e);
        }
    }
}
