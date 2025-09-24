package com.example.petner.search.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostEvent {
    private final Long postId;
    private final EventType eventType;

    public static PostEvent created(Long postId) {
        return new PostEvent(postId, EventType.CREATED);
    }

    public static PostEvent updated(Long postId) {
        return new PostEvent(postId, EventType.UPDATED);
    }

    public static PostEvent deleted(Long postId) {
        return new PostEvent(postId, EventType.DELETED);
    }

    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}