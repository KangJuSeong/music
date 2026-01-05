package com.example.music.service;

import com.example.music.dto.ArtistAlbumCountDto;
import com.example.music.dto.YearlyAlbumCountDto;
import com.example.music.repository.album.AlbumRepository;
import com.example.music.repository.artist.ArtistRepository;
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
    private final ArtistRepository artistRepository;

    public AlbumStatisticsService(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public Mono<Page<YearlyAlbumCountDto>> getAlbumsCountByYearPageable(Pageable pageable) {
        return Mono.zip(
                albumRepository.findAlbumCountsByYearly(pageable)
                        .collectList(),
                albumRepository.countDistinctYearForAlbums()
        ).map(t  -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    public Mono<Page<ArtistAlbumCountDto>> getAlbumsCountByArtistPageable(Pageable pageable) {
        return Mono.zip(
                artistRepository.findAlbumCountByArtist(pageable)
                        .collectList(),
                artistRepository.countDistinctArtistForAlbums()
        ).map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }
}