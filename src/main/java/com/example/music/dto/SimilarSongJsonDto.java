package com.example.music.dto;

import com.example.music.entity.SimilarSongEntity;
import lombok.Data;

@Data
public class SimilarSongJsonDto {
    private String similarArtist;
    private String similarSong;
    private double similarScore;

    public SimilarSongEntity toSimilarSongEntity(Long songId) {
        return SimilarSongEntity.builder()
                .similarArtist(similarArtist)
                .similarSong(similarSong)
                .similarScore(similarScore)
                .songId(songId)
                .build();
    }
}
