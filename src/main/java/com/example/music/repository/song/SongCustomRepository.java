package com.example.music.repository.song;

import com.example.music.dto.SongResponseDto;
import com.example.music.entity.SongEntity;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SongCustomRepository {
    Flux<SongResponseDto> findAllByPageable(Pageable pageable);
}
