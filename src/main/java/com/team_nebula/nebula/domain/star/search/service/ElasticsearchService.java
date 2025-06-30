package com.team_nebula.nebula.domain.star.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.repository.StarSearchRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

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

    // 인덱스 삭제
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

    public List<SearchResultWithScore> searchStars(String keyword, Long userId, int page, int size) {
        try {
            MultiMatchQuery multiMatchQuery = createMultiMatchQuery(keyword);
            Query userQuery = createUserQuery(userId);
            BoolQuery boolQuery = createBoolQuery(multiMatchQuery, userQuery);
            SearchRequest searchRequest = createSearchRequest(boolQuery, page, size);
            return executeSearch(searchRequest);
        } catch (Exception e) {
            log.error("Failed to search stars : " + keyword, e);
            return List.of();
        }
    }

    private MultiMatchQuery createMultiMatchQuery(String keyword) {
        return MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("title^3", "summaryAI^2", "userMemo^1", "keywords^2", "allContent^1", "categoryName^2")
                .fuzziness("AUTO")
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

    private BoolQuery createBoolQuery(MultiMatchQuery multiMatchQuery, Query userQuery) {
        return BoolQuery.of(b -> b
                .must(Query.of(q -> q.multiMatch(multiMatchQuery)))
                .filter(userQuery)
        );
    }

    private SearchRequest createSearchRequest(BoolQuery boolQuery, int page, int size) {
        return SearchRequest.of(s -> s
                .index("star_search")
                .query(Query.of(q -> q.bool(boolQuery)))
                .from(page * size)
                .size(size)
                .sort(sort -> sort
                        .score(sc -> sc.order(SortOrder.Desc))
                )
        );
    }

    private List<SearchResultWithScore> executeSearch(SearchRequest searchRequest) throws Exception {
        SearchResponse<StarSearchDocument> response = elasticsearchClient.search(searchRequest, StarSearchDocument.class);

        return response.hits().hits().stream()
                .map(hit -> new SearchResultWithScore(hit.source(), hit.score()))
                .collect(Collectors.toList());
    }

    public record SearchResultWithScore(StarSearchDocument document, Double score) {}



    public List<String> getAutoComplete(String query, Long userId, int size) {
        try {
            Query prefixQuery = Query.of(q -> q
                    .bool(b -> b
                            .must(m -> m
                                    .multiMatch(mm -> mm
                                            .query(query)
                                            .fields("title", "keywords", "summaryAI", "categoryName")
                                            .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.PhrasePrefix)
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

            return response.hits().hits().stream()
                    .map(hit -> hit.source().getTitle())
                    .distinct()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Auto complete failed for query: {}", query, e);
            return List.of();
        }
    }
}
