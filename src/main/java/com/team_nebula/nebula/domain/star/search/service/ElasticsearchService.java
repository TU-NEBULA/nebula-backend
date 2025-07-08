package com.team_nebula.nebula.domain.star.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.repository.StarSearchRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

    @Value("${elasticsearch.index.star_search}")
    private String starSearchIndex;

    private final ElasticsearchClient elasticsearchClient;
    private final StarSearchRepository starSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void indexDocument(StarSearchDocument document)  {
        try {
            starSearchRepository.save(document);
            log.info("Document indexed successfully : " + document.getId());
        }   catch (Exception e) {
            log.error("Failed to index document : " + document.getId(), e);
        }
    }

    public void deleteDocument(String documentId)  {
        try {
            starSearchRepository.deleteById(documentId);
            log.info("Document deleted successfully : " + documentId);
        }   catch (Exception e) {
            log.error("Failed to delete document : " + documentId, e);
        }
    }

    public void deleteIndex(String indexName) {
        try {
            IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            if (indexOperations.exists()) {
                boolean deleted = indexOperations.delete();
                if (deleted) {
                    log.info("Successfully deleted index: {}", indexName);
                } else {
                    log.warn("Failed to delete index: {}", indexName);
                }
            } else {
                log.info("Index does not exist: {}", indexName);
            }
        } catch (Exception e) {
            log.error("Error deleting index: {}", indexName, e);
        }
    }

    public SearchResultsWithCount searchStars(String keyword, Long userId, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new SearchResultsWithCount(Collections.emptyList(), 0);
        }
        if (userId == null || page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid search parameters");
        }

        try {
            Query matchQuery = createOptimizedQuery(keyword);
            Query userQuery = createUserQuery(userId);
            BoolQuery boolQuery = createBoolQuery(matchQuery, userQuery);
            SearchRequest searchRequest = createSearchRequest(boolQuery, page, size);

            SearchResponse<StarSearchDocument> response = elasticsearchClient.search(searchRequest, StarSearchDocument.class);

            List<SearchResultWithScore> results = response.hits().hits().stream()
                    .map(hit -> new SearchResultWithScore(hit.source(), hit.score()))
                    .collect(Collectors.toList());

            long totalCount = response.hits().total().value();

            return new SearchResultsWithCount(results, totalCount);
        } catch (Exception e) {
            log.error("Failed to search stars : " + keyword, e);
            return new SearchResultsWithCount(Collections.emptyList(), 0);
        }
    }

    private Query createOptimizedQuery(String keyword) {
        return Query.of(q -> q
                .match(m -> m
                        .field("allContent")
                        .query(keyword)
                        .fuzziness("AUTO")
                )
        );
    }

    private Query createUserQuery(Long userId) {
        return Query.of(q -> q
                .term(t -> t
                        .field("userId")
                        .value(userId)
                )
        );
    }

    private BoolQuery createBoolQuery(Query matchQuery, Query userQuery) {
        return BoolQuery.of(b -> b
                .must(matchQuery)
                .filter(userQuery)
        );
    }

    private SearchRequest createSearchRequest(BoolQuery boolQuery, int page, int size) {
        return SearchRequest.of(s -> s
                .index(starSearchIndex)
                .query(Query.of(q -> q.bool(boolQuery)))
                .from(page * size)
                .size(size)
                .sort(sort -> sort
                        .score(sc -> sc.order(SortOrder.Desc))
                )
        );
    }

    @Cacheable(value = "autocomplete_service", key = "T(String).format('%s:%d:%d', #query, #userId, #size)")
    public List<String> getAutoComplete(String query, Long userId, int size) {
        try {
            Query prefixQuery = Query.of(q -> q
                    .bool(b -> b
                            .must(m -> m
                                    .matchPhrasePrefix(mpp -> mpp
                                            .field("allContent")
                                            .query(query)
                                    )
                            )
                            .filter(f -> f
                                    .term(t -> t.field("userId").value(userId))
                            )
                    )
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("star_search")
                    .query(prefixQuery)
                    .size(size)
            );

            SearchResponse<StarSearchDocument> response = elasticsearchClient.search(searchRequest, StarSearchDocument.class);

            List<String> results = response.hits().hits().stream()
                    .map(hit -> hit.source().getTitle())
                    .distinct()
                    .collect(Collectors.toList());

            return results;

        } catch (Exception e) {
            log.error("Auto complete failed for query: {}", query, e);
            return List.of();
        }
    }

    // Records
    public record SearchResultWithScore(StarSearchDocument document, Double score) {}
    public record SearchResultsWithCount(List<SearchResultWithScore> results, long totalCount) {}
}
