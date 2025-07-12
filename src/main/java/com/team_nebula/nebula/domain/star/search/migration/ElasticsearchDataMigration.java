package com.team_nebula.nebula.domain.star.search.migration;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.*;
import org.opensearch.client.opensearch.indices.update_aliases.Action;
import org.opensearch.client.opensearch.indices.update_aliases.AddAction;
import org.opensearch.client.opensearch.indices.update_aliases.RemoveAction;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import com.team_nebula.nebula.domain.star.search.listener.StarSearchEventListener;
import lombok.RequiredArgsConstructor;
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
@ConditionalOnProperty(name = "spring.opensearch.migration.enabled", havingValue = "true", matchIfMissing = false)
public class ElasticsearchDataMigration {

    private final StarRepository starRepository;
    private final OpenSearchClient openSearchClient;

    private static final String INDEX_ALIAS = "star_search";
    private static final String INDEX_PATTERN = "star_search_v";

    @EventListener(ApplicationReadyEvent.class)
    @Async("taskExecutor")
    public void migrateExistingData() {
        log.info("Starting safe OpenSearch data migration...");

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

    private String generateNewIndexName() {
        return INDEX_PATTERN + System.currentTimeMillis();
    }

    private void createNewIndex(String indexName) throws Exception {
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("0")
                )
        );

        openSearchClient.indices().create(request);
        log.info("Successfully created new index: {}", indexName);
    }

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

    private void indexDocumentToSpecificIndex(StarSearchDocument document, String indexName) throws Exception {
        IndexRequest<StarSearchDocument> request = IndexRequest.of(i -> i
                .index(indexName)
                .id(document.getId())
                .document(document)
        );

        openSearchClient.index(request);
    }

    private void refreshIndex(String indexName) {
        try {
            RefreshRequest request = RefreshRequest.of(r -> r.index(indexName));
            openSearchClient.indices().refresh(request);
            log.info("Refreshed index: {}", indexName);
        } catch (Exception e) {
            log.error("Failed to refresh index: {}", indexName, e);
        }
    }

    private boolean verifyMigration(String newIndexName, int expectedCount) {
        try {
            // 새 인덱스의 문서 수 확인
            CountRequest countRequest = CountRequest.of(c -> c.index(newIndexName));
            CountResponse countResponse = openSearchClient.count(countRequest);

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
                    openSearchClient.search(searchRequest, StarSearchDocument.class);

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

    private void switchAliasToNewIndex(String newIndexName) throws Exception {
        List<Action> actions = new ArrayList<>();

        try {
            // 먼저 기존 star_search 인덱스가 있는지 확인하고 삭제
            GetIndexRequest getIndexRequest = GetIndexRequest.of(g -> g.index(INDEX_ALIAS));
            GetIndexResponse indexResponse = openSearchClient.indices().get(getIndexRequest);

            // 기존 인덱스가 존재하면 삭제
            if (!indexResponse.result().isEmpty()) {
                log.info("Found existing index with alias name: {}. Deleting it first.", INDEX_ALIAS);
                DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(INDEX_ALIAS));
                openSearchClient.indices().delete(deleteRequest);
                log.info("Deleted existing index: {}", INDEX_ALIAS);
            }

        } catch (OpenSearchException e) {
            // 인덱스가 없으면 무시 (정상 상황)
            log.info("No existing index found with name: {}", INDEX_ALIAS);
        }

        try {
            // 기존 alias 조회 및 제거 액션 추가
            GetAliasRequest getAliasRequest = GetAliasRequest.of(g -> g.name(INDEX_ALIAS));
            GetAliasResponse aliasResponse = openSearchClient.indices().getAlias(getAliasRequest);

            for (String indexName : aliasResponse.result().keySet()) {
                actions.add(Action.of(a -> a
                        .remove(RemoveAction.of(r -> r.index(indexName).alias(INDEX_ALIAS)))
                ));
            }
        } catch (OpenSearchException e) {
            log.info("No existing alias found, creating new one");
        }

        // 새 인덱스에 alias 추가
        actions.add(Action.of(a -> a
                .add(AddAction.of(add -> add.index(newIndexName).alias(INDEX_ALIAS)))
        ));

        // 원자적 alias 전환 실행
        UpdateAliasesRequest updateRequest = UpdateAliasesRequest.of(u -> u.actions(actions));
        openSearchClient.indices().updateAliases(updateRequest);

        log.info("Successfully switched alias '{}' to new index '{}'", INDEX_ALIAS, newIndexName);
    }

    private void cleanupOldIndices(String currentIndexName) {
        try {
            GetIndexRequest getIndexRequest = GetIndexRequest.of(g -> g.index(INDEX_PATTERN + "*"));
            GetIndexResponse indexResponse = openSearchClient.indices().get(getIndexRequest);

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

    private boolean isIndexLinkedToAlias(String indexName) {
        try {
            GetAliasRequest request = GetAliasRequest.of(g -> g.index(indexName));
            GetAliasResponse response = openSearchClient.indices().getAlias(request);
            return !response.result().get(indexName).aliases().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void deleteOldIndexWithDelay(String indexName) {
        CompletableFuture.delayedExecutor(5, TimeUnit.MINUTES).execute(() -> {
            try {
                DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(indexName));
                openSearchClient.indices().delete(deleteRequest);
                log.info("Deleted old index: {}", indexName);
            } catch (Exception e) {
                log.warn("Failed to delete old index: {}", indexName, e);
            }
        });
    }

    private void rollbackMigration(String newIndexName) {
        try {
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(newIndexName));
            openSearchClient.indices().delete(deleteRequest);
            log.info("Rolled back migration: deleted failed index {}", newIndexName);
        } catch (Exception e) {
            log.error("Failed to rollback migration", e);
        }
    }
}