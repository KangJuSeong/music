package com.example.music.controller;

import com.example.music.dto.YearlyAlbumCountDto;
import com.example.music.service.AlbumStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/music/statistics")
@Slf4j
public class MusicStatisticsController {
    private final AlbumStatisticsService albumStatisticsService;

    public MusicStatisticsController(AlbumStatisticsService albumStatisticsService) {
        this.albumStatisticsService = albumStatisticsService;
    }

    @GetMapping("/album/yearly/counts")
    public Mono<Page<YearlyAlbumCountDto>> getYearlyAlbumsCounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Request yearly album counts - pageable {}", pageable);
        return albumStatisticsService.getAlbumsCountByYearPageable(pageable);
    }
}
