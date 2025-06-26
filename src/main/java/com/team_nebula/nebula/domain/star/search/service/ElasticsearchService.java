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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final StarSearchRepository starSearchRepository;
    private final HikariDataSource dataSource;

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

    /**
     * 다중 필드 검색 쿼리 생성
     */
    private MultiMatchQuery createMultiMatchQuery(String keyword) {
        return MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("title^3", "summaryAI^2", "userMemo^1", "keywords^2", "allContent^1")
                .fuzziness("AUTO")
        );
    }

    /**
     * 사용자 필터링 쿼리 생성
     */
    private Query createUserQuery(Long userId) {
        return Query.of(q -> q
                .term(t -> t
                        .field("userId")
                        .value(userId)
                )
        );
    }

    /**
     * Bool 쿼리 조합 (검색 쿼리 + 사용자 필터)
     */
    private BoolQuery createBoolQuery(MultiMatchQuery multiMatchQuery, Query userQuery) {
        return BoolQuery.of(b -> b
                .must(Query.of(q -> q.multiMatch(multiMatchQuery)))
                .filter(userQuery)
        );
    }

    /**
     * SearchRequest 생성 (페이징 및 정렬 포함)
     */
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

    /**
     * Elasticsearch 검색 실행 및 결과 매핑 (점수 포함)
     */
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
                                            .fields("title", "keywords")
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
