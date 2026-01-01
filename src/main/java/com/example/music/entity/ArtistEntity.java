package com.example.music.entity;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("artists")
@Data
@Builder
@AllArgsConstructor
public class ArtistEntity {
    @Id
    private Long artistId;
    private String artistName;
}
