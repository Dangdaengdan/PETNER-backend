package com.example.petner.search.listener;

import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.search.document.PostDocument;
import com.example.petner.search.dto.PostSearchDto;
import com.example.petner.search.event.PostEvent;
import com.example.petner.search.repository.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchSyncListener {

    private final PostSearchRepository postSearchRepository;
    private final PostRepository postRepository;

    @TransactionalEventListener
    @Async("searchSyncExecutor")
    public void handlePostEvent(PostEvent event) {
        try {
            switch (event.getEventType()) {
                case CREATED:
                case UPDATED:
                    syncPostToSearch(event.getPostId());
                    break;
                case DELETED:
                    deletePostFromSearch(event.getPostId());
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to sync post search data for postId: {}", event.getPostId(), e);
        }
    }

    private void syncPostToSearch(Long postId) throws IOException {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        PostSearchDto postDto = PostSearchDto.from(post);
        PostDocument document = PostDocument.from(postDto);
        postSearchRepository.save(document);

        log.info("Post synced to search: {}", postId);
    }

    private void deletePostFromSearch(Long postId) throws IOException {
        postSearchRepository.delete(String.valueOf(postId));
        log.info("Post deleted from search: {}", postId);
    }
}