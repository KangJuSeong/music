package com.example.music.dto;

import com.example.music.dto.deserializer.SimilarSongDeserializer;
import com.example.music.dto.deserializer.YesNoBooleanDeserializer;
import com.example.music.dto.deserializer.ZeroOneBooleanDeserializer;
import com.example.music.entity.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SongJsonDto {
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

    public SongEntity toSongEntity(Long albumId, Long artistId) {
        return SongEntity.builder()
                .songName(song)
                .genre(genre)
                .length(length)
                .explicit(explicit)
                .lyrics(lyrics)
                .artistId(artistId)
                .albumId(albumId)
                .build();
    }

    public AlbumEntity toAlbumEntity() {
        return AlbumEntity.builder()
                .albumName(album)
                .releaseDate(releaseDate)
                .releaseYear(releaseDate != null ? String.valueOf(releaseDate.getYear()) : null)
                .build();
    }

    public ArtistEntity toArtistEntity() {
        return ArtistEntity.builder()
                .artistName(artist)
                .build();
    }

    public SongFeatureEntity toSongFeatureEntity(Long songId) {
        return SongFeatureEntity.builder()
                .emotion(emotion)
                .keySignature(keySignature)
                .timeSignature(timeSignature)
                .tempo(tempo)
                .loudness(loudness)
                .songId(songId)
                .build();
    }

    public SongMoodEntity toSongMoodEntity(Long songId) {
        return SongMoodEntity.builder()
                .popularity(popularity)
                .energy(energy)
                .danceability(danceability)
                .speechiness(speechiness)
                .liveness(liveness)
                .acousticness(acousticness)
                .instrumentalness(instrumentalness)
                .songId(songId)
                .build();
    }

    public ListenContextEntity toListenContextEntity(Long songId) {
        return ListenContextEntity.builder()
                .party(party)
                .workOrStudy(workOrStudy)
                .relaxationOrMeditation(relaxationOrMeditation)
                .exercise(exercise)
                .running(running)
                .yogaStretching(yogaOrStretching)
                .driving(driving)
                .socialGathering(socialGatherings)
                .morningRoutine(morningRoutine)
                .songId(songId)
                .build();
    }
}
