package com.example.petner.search.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DogEvent {
    private final Long dogId;
    private final EventType eventType;

    public static DogEvent created(Long dogId) {
        return new DogEvent(dogId, EventType.CREATED);
    }

    public static DogEvent updated(Long dogId) {
        return new DogEvent(dogId, EventType.UPDATED);
    }

    public static DogEvent deleted(Long dogId) {
        return new DogEvent(dogId, EventType.DELETED);
    }

    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}