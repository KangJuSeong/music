package com.example.music.controller;

import com.example.music.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music")
@Slf4j
public class MusicController {
    private final MusicService songService;

    public MusicController(MusicService songService) {
        this.songService = songService;
    }

    @GetMapping("/init")
    public Mono<Void> initializeMusic() {
        log.debug("Request initialize music data");
        return songService.syncMusicMetadata();
    }
}
