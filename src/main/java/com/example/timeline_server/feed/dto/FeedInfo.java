package com.example.timeline_server.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedInfo {

    private int feedId;
    private String imageId;
    private int uploaderId;
    private String uploaderName;
    private ZonedDateTime uploadDatetime;
    private String contents;
}
