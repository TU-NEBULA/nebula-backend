package com.team_nebula.nebula.domain.star.search.batch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.google.common.collect.Lists;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchBatchService {

    private final ElasticsearchClient elasticsearchClient;

    @Async("batchExecutor")
    public CompletableFuture<Void> processBatchAsync(List<StarSearchDocument> documents) {
        try {
            batchIndexDocuments(documents);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Async batch processing failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public void batchIndexDocuments(List<StarSearchDocument> documents) {
        if (documents.isEmpty()) return;

        try {
            List<List<StarSearchDocument>> batches = Lists.partition(documents, 100);
            for (List<StarSearchDocument> batch : batches) {
                processBatch(batch);
                Thread.sleep(50);
            }
        } catch (Exception e) {
            log.error("Batch indexing failed", e);
        }
    }

    private void processBatch(List<StarSearchDocument> batch) throws Exception {
        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
        for (StarSearchDocument doc : batch) {
            bulkBuilder.operations(op -> op
                    .index(IndexOperation.of(i -> i
                            .index("star_search")
                            .id(doc.getId())
                            .document(doc)
                    ))
            );
        }

        BulkResponse response = elasticsearchClient.bulk(bulkBuilder.build());
        if (response.errors()) {
            log.error("Bulk indexing failed for some documents in batch");
            response.items().forEach(item -> {
                if (item.error() != null) {
                    log.error("Failed to index document {}: {}", item.id(), item.error().reason());
                }
            });
        } else {
            log.info("Successfully indexed {} documents", batch.size());
        }
    }
}

