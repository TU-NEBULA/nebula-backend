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

    public List<StarSearchDocument> searchStars(String keyword, Long userId, int page, int size) {
        try {
            // 다중 필드 검색 쿼리
            MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m
                    .query(keyword)
                    .fields("title^3", "summaryAI^2", "userMemo^1", "keywords^2", "allContent^1")
                    .fuzziness("AUTO")
            );

            // 사용자 필터링
            Query userQuery = Query.of(q -> q
                    .term(t -> t
                            .field("userId")
                            .value(userId)
                    )
            );

            // Bool 쿼리로 조합
            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .must(Query.of(q -> q.multiMatch(multiMatchQuery)))
                    .filter(userQuery)
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("star_search")
                    .query(Query.of(q -> q.bool(boolQuery)))
                    .from(page * size)
                    .size(size)
                    .sort(sort -> sort
                            .score(sc -> sc.order(SortOrder.Desc))
                    )
            );

            SearchResponse<StarSearchDocument> response = elasticsearchClient.search(searchRequest, StarSearchDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        }   catch (Exception e) {
            log.error("Failed to search stars : " + keyword, e);
            return List.of();
        }
    }


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
