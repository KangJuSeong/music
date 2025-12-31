package com.example.music.dto;

import com.example.music.dto.deserializer.SimilarSongDeserializer;
import com.example.music.dto.deserializer.YesNoBooleanDeserializer;
import com.example.music.dto.deserializer.ZeroOneBooleanDeserializer;
import com.example.music.entity.AlbumEntity;
import com.example.music.entity.SongEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MusicJsonDto {
    @JsonProperty("Artist(s)")
    private String artist;
    @JsonProperty("song")
    private String song;
    @JsonProperty("text")
    private String lyrics;
    @JsonProperty("Length")
    private String length;
    @JsonProperty("emotion")
    private String emotion;
    @JsonProperty("Genre")
    private String genre;
    @JsonProperty("Album")
    private String album;
    @JsonProperty("Release Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @JsonProperty("Key")
    private String keySignature;
    @JsonProperty("Tempo")
    private double tempo;
    @JsonProperty("Loudness (db)")
    private double loudness;
    @JsonProperty("Time signature")
    private String timeSignature;
    @JsonDeserialize(using = YesNoBooleanDeserializer.class)
    @JsonProperty("Explicit")
    private boolean explicit;
    @JsonProperty("Popularity")
    private int popularity;
    @JsonProperty("Energy")
    private int energy;
    @JsonProperty("Danceability")
    private int danceability;
    @JsonProperty("Positiveness")
    private int positiveness;
    @JsonProperty("Speechiness")
    private int speechiness;
    @JsonProperty("Liveness")
    private int liveness;
    @JsonProperty("Acousticness")
    private int acousticness;
    @JsonProperty("Instrumentalness")
    private int instrumentalness;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Party")
    private boolean party;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Work/Study")
    private boolean workOrStudy;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Relaxation/Meditation")
    private boolean relaxationOrMeditation;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Exercise")
    private boolean exercise;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Running")
    private boolean running;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Yoga/Stretching")
    private boolean yogaOrStretching;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Driving")
    private boolean driving;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Social Gatherings")
    private boolean socialGatherings;
    @JsonDeserialize(using = ZeroOneBooleanDeserializer.class)
    @JsonProperty("Good for Morning Routine")
    private boolean morningRoutine;
    @JsonDeserialize(contentUsing = SimilarSongDeserializer.class)
    @JsonProperty("Similar Songs")
    private List<SimilarSongJsonDto> similarSongs;
}
