package com.example.music.dto;

import lombok.Data;

@Data
public class SimilarSongJsonDto {
    private String artist;
    private String similarSong;
    private double score;
}
