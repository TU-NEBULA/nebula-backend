package com.team_nebula.nebula.domain.star.search.service;

import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.repository.StarSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

    @Value("${opensearch.index.star_search:star_search}")
    private String starSearchIndex;

    private final OpenSearchClient openSearchClient;
    private final StarSearchRepository starSearchRepository;

    public void indexDocument(StarSearchDocument document) {
        try {
            starSearchRepository.save(document);
            log.info("Document indexed successfully : " + document.getId());
        } catch (Exception e) {
            log.error("Failed to index document : " + document.getId(), e);
        }
    }

    public void deleteDocument(String documentId) {
        try {
            starSearchRepository.deleteById(documentId);
            log.info("Document deleted successfully : " + documentId);
        } catch (Exception e) {
            log.error("Failed to delete document : " + documentId, e);
        }
    }

    public void deleteIndex(String indexName) {
        try {
            // OpenSearch 클라이언트로 인덱스 존재 여부 확인
            ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(indexName));
            boolean exists = openSearchClient.indices().exists(existsRequest).value();

            if (exists) {
                DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(indexName));
                var response = openSearchClient.indices().delete(deleteRequest);

                if (response.acknowledged()) {
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

    // 기존 searchStars 메서드 (V2에서 사용)
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

            SearchResponse<StarSearchDocument> response = openSearchClient.search(searchRequest, StarSearchDocument.class);

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
                        .query(FieldValue.of(keyword))
                        .fuzziness("AUTO")
                )
        );
    }

    private Query createUserQuery(Long userId) {
        return Query.of(q -> q
                .term(t -> t
                        .field("userId")
                        .value(FieldValue.of(userId))
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
                                    .term(t -> t.field("userId").value(FieldValue.of(userId)))
                            )
                    )
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("star_search")
                    .query(prefixQuery)
                    .size(size)
            );

            SearchResponse<StarSearchDocument> response = openSearchClient.search(searchRequest, StarSearchDocument.class);
            
            return response.hits().hits().stream()
                    .map(hit -> hit.source().getTitle())
                    .distinct()
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Auto complete failed for query: {}", query, e);
            return List.of();
        }
    }

    // 새로운 검색 메서드 (다른 용도로 사용)
    @Cacheable(value = "starSearch", key = "#userId + '_' + #keyword + '_' + #page + '_' + #size")
    public List<StarSearchDocument> searchStarsSimple(Long userId, String keyword, int page, int size) {
        try {
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            
            // 사용자 ID 필터
            boolQuery.must(Query.of(q -> q
                .term(t -> t
                    .field("userId")
                    .value(FieldValue.of(userId))
                )
            ));

            // 키워드 검색 (여러 필드에서 검색)
            if (keyword != null && !keyword.trim().isEmpty()) {
                boolQuery.must(Query.of(q -> q
                    .multiMatch(m -> m
                        .query(keyword)
                        .fields("title^2", "allContent", "categoryName", "summaryAI", "userMemo", "keywords")
                    )
                ));
            }

            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(starSearchIndex)
                .query(Query.of(q -> q.bool(boolQuery.build())))
                .from(page * size)
                .size(size)
                .sort(sort -> sort
                    .field(f -> f
                        .field("lastAccessedAt")
                        .order(SortOrder.Desc)
                    )
                )
            );

            SearchResponse<StarSearchDocument> response = openSearchClient.search(searchRequest, StarSearchDocument.class);
            
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Failed to search stars for user: {}, keyword: {}", userId, keyword, e);
            return Collections.emptyList();
        }
    }

    // Records
    public record SearchResultWithScore(StarSearchDocument document, Double score) {}
    public record SearchResultsWithCount(List<SearchResultWithScore> results, long totalCount) {}
}