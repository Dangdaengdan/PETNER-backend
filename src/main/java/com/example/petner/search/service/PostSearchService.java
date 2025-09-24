package com.example.petner.search.service;

import com.example.petner.search.document.PostDocument;
import com.example.petner.search.repository.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostSearchService {

    private final PostSearchRepository postSearchRepository;

    public List<PostDocument> searchPosts(String keyword, String sortBy, int page, int size) {
        try {
            int from = page * size;
            return postSearchRepository.searchByKeyword(keyword, sortBy, from, size);
        } catch (IOException e) {
            log.error("Failed to search posts", e);
            throw new RuntimeException("검색 중 오류가 발생했습니다.", e);
        }
    }
}