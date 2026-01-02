package com.example.music.controller;

import com.example.music.service.MusicSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music/sync")
@Slf4j
public class MusicController {
    private final MusicSyncService songService;

    public MusicController(MusicSyncService songService) {
        this.songService = songService;
    }

    @GetMapping
    public Mono<Void> initializeMusic() {
        log.debug("Request initialize music data");
        return songService.syncMusicData();
    }
}
