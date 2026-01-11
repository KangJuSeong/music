package com.example.music.dto;

import com.example.music.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponseDto {
    @JsonProperty("song")
    SongEntity songEntity;

    @JsonProperty("artist")
    ArtistEntity artistEntity;

    @JsonProperty("album")
    AlbumEntity albumEntity;

    @JsonProperty("feature")
    @JsonInclude(JsonInclude.Include.NON_NULL)

    SongFeatureEntity songFeatureEntity;
    @JsonProperty("mood")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    SongMoodEntity songMoodEntity;

    @JsonProperty("listenContext")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ListenContextEntity listenContextEntity;

    @JsonProperty("similarSongs")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SimilarSongEntity> similarSongEntities;
}
