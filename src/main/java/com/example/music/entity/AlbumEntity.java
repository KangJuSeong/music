package com.example.music.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("albums")
@Data
@Builder
public class AlbumEntity {
    @Id
    private Long albumId;
    private String albumName;
    private LocalDate releaseDate;
    private String releaseYear;
}
