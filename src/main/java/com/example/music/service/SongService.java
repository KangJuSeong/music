package com.example.music.service;

import com.example.music.dto.SongResponseDto;
import com.example.music.repository.song.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SongService {

    private final SongRepository songRepository;

    public SongService (SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Mono<Page<SongResponseDto>> getSongList(Pageable pageable) {
        return Mono.zip(
                songRepository.findAllByPageable(pageable).collectList(),
                songRepository.countSongs()
        ).map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    public Mono<SongResponseDto> getSongDetail(Long songId) {
        return songRepository.findByIdForDetail(songId);
    }
}

