package com.example.timeline_server.timeline.controller;

import com.example.timeline_server.auth.AuthService;
import com.example.timeline_server.timeline.SocialPost;
import com.example.timeline_server.timeline.dto.LikeResponse;
import com.example.timeline_server.timeline.service.TimelineService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    private final AuthService authService;

    @GetMapping("/{userId}")
    public List<SocialPost> listFeed(
            @PathVariable("userId") String userId,
            @RequestParam(value = "followingFeed", required = false) boolean includeFollowingFeed
    ) {
        return includeFollowingFeed ? timelineService.listMyFeed(userId) : timelineService.listUserFeed(userId);
    }

    @GetMapping("/random")
    public List<SocialPost> listRandomFeeds() {
        return timelineService.getRandomFeeds();
    }

    @GetMapping("/like/{postId}")
    public LikeResponse likePost(@PathVariable("postId") int postId, HttpServletRequest request) {
        int userId = authService.getUserIdFromAuthServer(request);
        boolean isLike = timelineService.likePost(userId, postId);
        Long count = timelineService.countLike(postId);
        return new LikeResponse(count, isLike);
    }
}