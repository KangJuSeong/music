package com.example.music.repository.songLike;

import com.example.music.dto.SongLikeDto;
import reactor.core.publisher.Flux;

public interface SongLikeCustomRepository {
    Flux<SongLikeDto> findTopLikeCount(Integer top);
}
