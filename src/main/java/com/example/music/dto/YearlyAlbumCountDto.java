package com.example.music.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YearlyAlbumCountDto {
    private String releaseYear;
    private int albumCount;
}
