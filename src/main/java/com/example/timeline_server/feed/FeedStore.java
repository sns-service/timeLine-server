package com.example.timeline_server.feed;

import com.example.timeline_server.feed.dto.FeedInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class FeedStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public FeedStore(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void savePost(FeedInfo post) {
        try {
            redis.opsForZSet().add("feed:" + post.getUploaderId(), objectMapper.writeValueAsString(post), post.getUploadDatetime().toEpochSecond());
            redis.opsForZSet().add("feed:all", objectMapper.writeValueAsString(post), post.getUploadDatetime().toEpochSecond());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FeedInfo> allFeed() {  // 전체 피드 조회
        Set<String> savedFeed = redis.opsForZSet().reverseRange("feed:all", 0, -1);
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, FeedInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public List<FeedInfo> listFeed(String userId) {
        Set<String> savedFeed = redis.opsForZSet().reverseRange("feed:" + userId, 0, -1);
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, FeedInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

}