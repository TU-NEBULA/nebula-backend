package com.team_nebula.nebula.domain.star.search.service;

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
public class StarSearchAsyncBatchService {

    private final ElasticsearchBatchService batchService;

    @Async("batchExecutor")
    public CompletableFuture<Void> processBatchAsync(List<StarSearchDocument> documents) {
        try {
            batchService.batchIndexDocuments(documents);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Async batch processing failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
