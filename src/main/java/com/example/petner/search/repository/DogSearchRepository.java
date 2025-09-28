package com.example.petner.search.repository;

import com.example.petner.search.document.DogDocument;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.global.config.common.Gender;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DogSearchRepository {

    private static final String INDEX_NAME = "dogs";

    @Autowired
    private OpenSearchClient openSearchClient;

    public void save(DogDocument document) throws IOException {
        IndexRequest<DogDocument> request = IndexRequest.of(i -> i
                .index(INDEX_NAME)
                .id(document.getId())
                .document(document)
        );
        openSearchClient.index(request);
    }

    public void delete(String id) throws IOException {
        DeleteRequest request = DeleteRequest.of(d -> d
                .index(INDEX_NAME)
                .id(id)
        );
        openSearchClient.delete(request);
    }

    public Optional<DogDocument> findById(String id) throws IOException {
        GetRequest request = GetRequest.of(g -> g
                .index(INDEX_NAME)
                .id(id)
        );

        GetResponse<DogDocument> response = openSearchClient.get(request, DogDocument.class);
        return response.found() ? Optional.of(response.source()) : Optional.empty();
    }

    public List<DogDocument> searchWithFilters(String keyword,
                                              DogSize dogSize,
                                              String breedName,
                                              Gender gender,
                                              String location,
                                              AdoptionStatus adoptionStatus,
                                              int from,
                                              int size) throws IOException {

        org.opensearch.client.opensearch._types.query_dsl.BoolQuery.Builder boolQuery =
            new org.opensearch.client.opensearch._types.query_dsl.BoolQuery.Builder();

        // 키워드 검색: name과 description만 검색 대상
        if (keyword != null && !keyword.trim().isEmpty()) {
            Query keywordQuery = Query.of(q -> q.multiMatch(m -> m
                .query(keyword)
                .fields("name^2", "description")
                .analyzer("nori")
            ));
            boolQuery.must(keywordQuery);
        }

        // 필터 조건들 - 각각 있을 때만 추가
        if (dogSize != null) {
            boolQuery.filter(Query.of(q -> q.term(t -> t.field("dogSize").value(FieldValue.of(dogSize.name())))));
        }

        if (breedName != null && !breedName.trim().isEmpty()) {
            boolQuery.filter(Query.of(q -> q.match(m -> m.field("breedName").query(FieldValue.of(breedName)))));
        }

        if (gender != null) {
            boolQuery.filter(Query.of(q -> q.term(t -> t.field("gender").value(FieldValue.of(gender.name())))));
        }

        if (location != null && !location.trim().isEmpty()) {
            // Member location OR Shelter location 검색 - 부분 일치로 검색
            Query locationQuery = Query.of(q -> q.bool(b -> b
                .should(Query.of(sq -> sq.matchPhrase(m -> m.field("memberLocation").query(location))))
                .should(Query.of(sq -> sq.matchPhrase(m -> m.field("shelterLocation").query(location))))
                .should(Query.of(sq -> sq.matchPhrase(m -> m.field("location").query(location)))) // 기존 호환성
                .minimumShouldMatch("1")
            ));
            boolQuery.filter(locationQuery);
        }

        if (adoptionStatus != null) {
            boolQuery.filter(Query.of(q -> q.term(t -> t.field("adoptionStatus").value(FieldValue.of(adoptionStatus.name())))));
        }

        // 최종 쿼리 생성 - 빈 쿼리인 경우 matchAll 사용
        org.opensearch.client.opensearch._types.query_dsl.BoolQuery builtQuery = boolQuery.build();
        Query finalQuery;

        if (builtQuery.must().isEmpty() && builtQuery.filter().isEmpty()) {
            // 모든 조건이 비어있으면 전체 검색
            finalQuery = Query.of(q -> q.matchAll(m -> m));
        } else {
            finalQuery = Query.of(q -> q.bool(builtQuery));
        }

        // 키워드가 있으면 관련도순 + 생성일순, 없으면 생성일순만
        List<SortOptions> sortOptions = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))));
        } else {
            sortOptions.add(SortOptions.of(so -> so.score(s -> s.order(SortOrder.Desc))));
            sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))));
        }

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(finalQuery)
                .from(from)
                .size(size)
                .sort(sortOptions)
        );

        SearchResponse<DogDocument> response = openSearchClient.search(request, DogDocument.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}