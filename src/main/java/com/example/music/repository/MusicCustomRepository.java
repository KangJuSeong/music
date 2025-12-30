package com.example.music.repository;

import com.example.music.dto.MusicJsonDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MusicCustomRepository {
    Mono<Void> bulkInsert(List<MusicJsonDto> musicJsonDtoList);
}
