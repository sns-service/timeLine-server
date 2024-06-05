package com.example.timeline_server.feed;

import com.example.timeline_server.feed.dto.FeedInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public Long likePost(int userId, int postId) {
        return redis.opsForSet().add("likes:" + postId, String.valueOf(userId));
    }

    public Long unlikePost(int userId, int postId) {
        return redis.opsForSet().remove("likes:" + postId, String.valueOf(userId));
    }

    public Boolean isLikePost(int userId, int postId) {
        return redis.opsForSet().isMember("likes:"+postId, String.valueOf(userId));
    }

    public Long countLikes(int postId) {
        return redis.opsForSet().size("likes:" + postId);
    }

    public Map<Integer, Long> countLikes(List<Integer> postIds) {
        Map<Integer, Long> likesMap = new HashMap<>();

        List<Object> results = redis.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

            for (int postId : postIds) {
                stringRedisConn.sCard("likes:" + postId);
            }
            return null;
        });

        int index = 0;
        for (int postId : postIds) {
            Long likeCount = (Long) results.get(index++); // Get the result from the results list
            likesMap.put(postId, likeCount);
        }

        return likesMap;
    }

    public void savePost(FeedInfo post) {
        try {
            // Serialize post object to JSON string
            String postString = objectMapper.writeValueAsString(post);

            // Use feedId as the unique key
            String userFeedKey = "feed:" + post.getUploaderId() + ":" + post.getFeedId();
            String allFeedKey = "feed:all:" + post.getFeedId();

            Boolean addResult = redis.opsForZSet().add(userFeedKey, postString, post.getUploadDatetime().toEpochSecond());
            Boolean addResult2 = redis.opsForZSet().add(allFeedKey, postString, post.getUploadDatetime().toEpochSecond());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePost(FeedInfo post) {
        String userFeedKey = "feed:" + post.getUploaderId() + ":" + post.getFeedId();
        String allFeedKey = "feed:all:" + post.getFeedId();

        // Remove from sorted sets using the unique key
        Boolean removedFromUserFeed = redis.delete(userFeedKey);
        Boolean removedFromAllFeed = redis.delete(allFeedKey);
    }

    public List<FeedInfo> allFeed() {
        // Get all keys for the all feeds
        Set<String> keys = redis.keys("feed:all:*");

        // Fetch and combine all feed items for these keys
        Set<String> savedFeed = keys.stream()
                .flatMap(key -> redis.opsForZSet().reverseRange(key, 0, -1).stream())
                .collect(Collectors.toSet());

        // Convert JSON strings to FeedInfo objects
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, FeedInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<FeedInfo> listFeed(String userId) {
        // Get all keys for the user's feeds
        Set<String> keys = redis.keys("feed:" + userId + ":*");

        // Fetch and combine all feed items for these keys
        Set<String> savedFeed = keys.stream()
                .flatMap(key -> redis.opsForZSet().reverseRange(key, 0, -1).stream())
                .collect(Collectors.toSet());

        // Convert JSON strings to FeedInfo objects
        return savedFeed.stream().map(feed -> {
            try {
                return objectMapper.readValue(feed, FeedInfo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}