package com.team_nebula.nebula.domain.star.search.repository;

import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StarSearchRepositoryImpl implements StarSearchRepository {

    private final OpenSearchClient openSearchClient;
    private static final String INDEX_NAME = "star_search";

    @Override
    public void save(StarSearchDocument document) {
        try {
            IndexRequest<StarSearchDocument> request = IndexRequest.of(i -> i
                    .index(INDEX_NAME)
                    .id(document.getId())
                    .document(document)
            );
            
            openSearchClient.index(request);
            log.info("Document indexed successfully: {}", document.getId());
        } catch (Exception e) {
            log.error("Failed to index document: {}", document.getId(), e);
            throw new RuntimeException("Failed to index document", e);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(INDEX_NAME)
                    .id(id)
            );
            
            openSearchClient.delete(request);
            log.info("Document deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete document: {}", id, e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }

    @Override
    public StarSearchDocument findById(String id) {
        try {
            GetRequest request = GetRequest.of(g -> g
                    .index(INDEX_NAME)
                    .id(id)
            );
            
            GetResponse<StarSearchDocument> response = openSearchClient.get(request, StarSearchDocument.class);
            
            if (response.found()) {
                return response.source();
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get document: {}", id, e);
            throw new RuntimeException("Failed to get document", e);
        }
    }
} 