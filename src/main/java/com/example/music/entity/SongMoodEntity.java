package com.example.music.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("song_moods")
@Data
@Builder
public class SongMoodEntity {
    @Id
    private Long moodId;
    private int popularity;
    private int energy;
    private int danceability;
    private int positiveness;
    private int speechiness;
    private int liveness;
    private int acousticness;
    private int instrumentalness;
    private Long songId;
}
