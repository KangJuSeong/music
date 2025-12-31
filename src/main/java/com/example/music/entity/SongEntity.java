package com.example.music.entity;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("songs")
@Data
@Builder
public class SongEntity {
    @Id
    private Long songId;
    private String songName;
    private String length;
    private String genre;
    private String lyrics;
    private boolean explicit;
    private long albumId;
}