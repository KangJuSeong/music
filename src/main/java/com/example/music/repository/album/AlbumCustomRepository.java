package com.example.music.repository.album;

import com.example.music.dto.YearlyAlbumCountDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface AlbumCustomRepository {
    Flux<YearlyAlbumCountDto> findAlbumCountsByYearly(Pageable pageable);
}
