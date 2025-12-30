package com.example.music.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("songs")
@Data
@Builder
public class SongEntity {
    private long songId;
    private String artist;
    private String length;
    private String emotion;
    private String genre;
    private String album;
    private String keySignature;
    private double tempo;
    private double loudness;
    private String timeSignature;
    private boolean explicit;
    private LocalDate releaseDate;
    private String lyrics;
}
