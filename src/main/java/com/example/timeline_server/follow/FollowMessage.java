package com.example.timeline_server.follow;

public class FollowMessage {

    // followerId -> userId
    private String userId;
    private String followerId;
    private boolean follow;

    public FollowMessage(String userId, String followerId, boolean follow) {
        this.userId = userId;
        this.followerId = followerId;
        this.follow = follow;
    }

    public String getUserId() {
        return userId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public boolean isFollow() {
        return follow;
    }
}