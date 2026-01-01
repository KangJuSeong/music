package com.example.music.dto;

import lombok.Data;

@Data
public class MusicSyncContext {
    private SongJsonDto dto;
    private Long albumId;
    private Long artistId;
    private Long songId;

    public MusicSyncContext(SongJsonDto dto) {
        this.dto = dto;
    }
}
