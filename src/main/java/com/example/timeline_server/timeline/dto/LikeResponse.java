package com.example.timeline_server.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
    long likeCount;
    boolean like;
}