package com.example.music.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Table("artists_songs")
@Data
@AllArgsConstructor
public class ArtistSongEntity {
    private Long artistId;
    private Long songId;
}
