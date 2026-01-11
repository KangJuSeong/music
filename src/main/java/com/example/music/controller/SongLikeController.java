package com.example.music.controller;

import com.example.music.dto.SongLikeDto;
import com.example.music.entity.SongEntity;
import com.example.music.service.SongLikeService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music/like")
public class SongLikeController {
    private final SongLikeService songLikeService;

    public SongLikeController(SongLikeService songLikeService) {
        this.songLikeService = songLikeService;
    }

    @PostMapping
    public Mono<Void> likeSong(@RequestBody SongEntity song) {
        return songLikeService.insertSongLike(song.getSongId());
    }

    @GetMapping("/top")
    public Flux<SongLikeDto> topSongLikesInHour(@RequestParam(defaultValue = "10") String top) {
        Integer topToInt = Integer.parseInt(top);
        return songLikeService.getSongLikeIncreaseTop(topToInt);
    }
}
