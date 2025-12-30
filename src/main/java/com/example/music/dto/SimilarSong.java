package com.example.music.dto;

import lombok.Data;

@Data
public class SimilarSong {
    private String artist;
    private String similarSong;
    private double score;
}
