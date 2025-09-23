package com.example.petner.search.repository;

import com.example.petner.search.document.PostDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
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
public class PostSearchRepository {

    private static final String INDEX_NAME = "posts";

    @Autowired
    private OpenSearchClient openSearchClient;

    public void save(PostDocument document) throws IOException {
        IndexRequest<PostDocument> request = IndexRequest.of(i -> i
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

    public Optional<PostDocument> findById(String id) throws IOException {
        GetRequest request = GetRequest.of(g -> g
                .index(INDEX_NAME)
                .id(id)
        );

        GetResponse<PostDocument> response = openSearchClient.get(request, PostDocument.class);
        return response.found() ? Optional.of(response.source()) : Optional.empty();
    }

    public List<PostDocument> searchByKeyword(String keyword, String sortBy, int from, int size) throws IOException {
        Query query = keyword != null && !keyword.trim().isEmpty()
            ? Query.of(q -> q.multiMatch(m -> m
                .query(keyword)
                .fields("title^2", "content", "authorName")
                .analyzer("nori_analyzer")
            ))
            : Query.of(q -> q.matchAll(m -> m));

        // 정렬 로직 시작
        List<SortOptions> sortOptions = new ArrayList<>();

        switch (sortBy) {
            case "latest":
                sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))));
                break;
            case "oldest":
                sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Asc))));
                break;
            case "viewCount":
                sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("viewCount").order(SortOrder.Desc))));
                sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))));
                break;
            default:
                sortOptions.add(SortOptions.of(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc))));
                break;
        }

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(query)
                .from(from)
                .size(size)
                .sort(sortOptions)
        );

        SearchResponse<PostDocument> response = openSearchClient.search(request, PostDocument.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}