package com.example.music.service;

import com.example.music.dto.YearlyAlbumCountDto;
import com.example.music.repository.album.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class AlbumStatisticsService {
    private final AlbumRepository albumRepository;

    public AlbumStatisticsService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Mono<Page<YearlyAlbumCountDto>> getAlbumsCountByYearPageable(Pageable pageable) {
        return Mono.zip(
                albumRepository.findAlbumCountsByYearly(pageable)
                        .collectList(),
                albumRepository.countDistinctYearForAlbums()
        ).map(t  -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }
}