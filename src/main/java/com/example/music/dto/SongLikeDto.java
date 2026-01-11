package com.example.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SongLikeDto {
    private Long songId;
    private String songName;
    private Long likes;
}
