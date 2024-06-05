package com.example.timeline_server.feed;

import com.example.timeline_server.feed.dto.FeedInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class FeedListener {

    private final ObjectMapper objectMapper;
    private final FeedStore feedStore;

    public FeedListener(ObjectMapper objectMapper, FeedStore feedStore) {
        this.objectMapper = objectMapper;
        this.feedStore = feedStore;
    }

    @KafkaListener(topics = "feed.created", groupId = "timeline-server")
    public void listen(String message) {
        try {
            FeedInfo feed = objectMapper.readValue(message, FeedInfo.class);
            feedStore.savePost(feed);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "feed.deleted", groupId = "timeline-server")
    public void listenFeedDeleted(String message) {
        try {
            FeedInfo feed = objectMapper.readValue(message, FeedInfo.class);
            feedStore.removePost(feed);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}