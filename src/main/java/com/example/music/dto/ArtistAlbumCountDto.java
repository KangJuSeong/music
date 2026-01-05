package com.example.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArtistAlbumCountDto {
    private String artistName;
    private Long albumCount;
}
