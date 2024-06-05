package com.example.timeline_server.timeline;

import com.example.timeline_server.feed.dto.FeedInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialPost {

    private int feedId;
    private String imageId;
    private String uploaderName;
    private int uploaderId;

    private ZonedDateTime uploadDatetime;
    private String contents;
    private Long likes;

    public SocialPost(FeedInfo post, Long likes) {
        this(post.getFeedId(), post.getImageId(), post.getUploaderName(), post.getUploaderId(), post.getUploadDatetime(), post.getContents(), likes == null ? 0 : likes);
    }
}