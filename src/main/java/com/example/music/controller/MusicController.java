package com.example.music.controller;

import com.example.music.dto.SongResponseDto;
import com.example.music.service.MusicSyncService;
import com.example.music.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music")
@Slf4j
public class MusicController {
    private final MusicSyncService syncService;
    private final SongService songService;

    public MusicController(MusicSyncService syncService, SongService songService) {
        this.syncService = syncService;
        this.songService = songService;
    }

    @GetMapping("/sync")
    public Mono<Void> initializeMusic() {
        log.debug("Request initialize music data");
        return syncService.syncMusicData();
    }

    @GetMapping("/list")
    public Mono<Page<SongResponseDto>> getMusicList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "song_id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = Sort.by(sortBy);
        sort = direction.equals("desc") ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return songService.getSongList(pageable);
    }

    @GetMapping("/{id}")
    public Mono<Void> getMusicDetail() {
        throw new UnsupportedOperationException();
    }
}
