package com.example.music.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("song_features")
@Data
@Builder
public class SongFeatureEntity {
    @Id
    private Long  featureId;
    private String emotion;
    private String keySignature;
    private String timeSignature;
    private double tempo;
    private double loudness;
    private Long  songId;
}
