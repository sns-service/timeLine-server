package com.example.timeline_server.timeline.service;

import com.example.timeline_server.feed.FeedStore;
import com.example.timeline_server.feed.dto.FeedInfo;
import com.example.timeline_server.follow.FollowerStore;
import com.example.timeline_server.timeline.SocialPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final FeedStore feedStore;
    private final FollowerStore followerStore;

    public List<SocialPost> listUserFeed(String userId) {
        List<FeedInfo> feedList = feedStore.listFeed(userId);
        Map<Integer, Long> likes = feedStore.countLikes(feedList.stream().map(FeedInfo::getFeedId).toList());
        return feedList.stream().map(
                post -> new SocialPost(post, likes.getOrDefault(post.getFeedId(), 0L))
        ).toList();
    }

    public List<SocialPost> getRandomFeeds() {
        List<SocialPost> postList = listAllFeed();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < postList.size(); i++) {
            indices.add(i);
        }

        Collections.shuffle(indices, new Random());
        List<SocialPost> randomPosts = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            randomPosts.add(postList.get(indices.get(i)));
        }

        return randomPosts;
    }

    public List<SocialPost> listFollowerFeed(Set<String> followerSet) {
        return followerSet
                .stream()
                .map(this::listUserFeed)
                .filter(Objects::nonNull) // Filter out any nulls that might have resulted from invalid ids
                .flatMap(List::stream) // Flatten the stream of lists into a stream of SocialPost
                .collect(Collectors.toList());
    }

    public List<SocialPost> listMyFeed(String userId) {
        Set<String> followers = followerStore.listFollows(String.valueOf(userId));
        List<SocialPost> myPost = listUserFeed(userId);
        List<SocialPost> followerFeed = listFollowerFeed(followers);

        return Stream.concat(myPost.stream(), followerFeed.stream())
                .sorted(Comparator.comparing(SocialPost::getUploadDatetime).reversed())
                .collect(Collectors.toList());
    }

    public List<SocialPost> listAllFeed() {
        List<FeedInfo> feedList = feedStore.allFeed();
        Map<Integer, Long> likes = feedStore.countLikes(feedList.stream().map(FeedInfo::getFeedId).toList());
        return feedList.stream().map(
                post -> new SocialPost(post, likes.getOrDefault(post.getFeedId(), 0L))
        ).toList();
    }
    public boolean likePost(int userId, int postId) {
        if (feedStore.isLikePost(userId, postId)) {
            feedStore.unlikePost(userId, postId);
            return false;
        } else {
            feedStore.likePost(userId, postId);
            return true;
        }
    }

    public Long countLike(int postId) {
        return feedStore.countLikes(postId);
    }
}