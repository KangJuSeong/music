package com.example.music.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("song_likes")
@Data
public class SongLikeEntity {
    @Id
    private Long songLikeId;
    private Long songId;
    private LocalDateTime createdAt;
}
