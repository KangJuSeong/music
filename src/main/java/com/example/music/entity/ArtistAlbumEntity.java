package com.example.music.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Table("artists_albums")
@Data
@AllArgsConstructor
public class ArtistAlbumEntity {
    private Long artistId;
    private Long albumId;
}
