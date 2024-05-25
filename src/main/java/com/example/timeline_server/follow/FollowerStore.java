package com.example.timeline_server.follow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FollowerStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public FollowerStore(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void followUser(FollowMessage followMessage) {
        if (followMessage.isFollow()) {
            redis.opsForSet().add("user:follower" + followMessage.getFollowerId(), followMessage.getUserId());
        } else {
            redis.opsForSet().remove("user:follower" + followMessage.getFollowerId(), followMessage.getUserId());
        }
    }

    public Set<String> listFollows(String userId) {  // 내가 팔로우 하고 있는 사용자 리스트 (내가 팔로워이다.)
        return redis.opsForSet().members("user:follower:"+userId);
    }
}