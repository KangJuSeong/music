package com.example.music.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("similar_songs")
@Data
@Builder
public class SimilarSongEntity {
    @Id
    private Long similarId;
    private String similarArtist;
    private String similarSong;
    private double similarScore;
    private Long songId;
}
