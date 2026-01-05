package com.example.music.repository.artist;

import com.example.music.dto.ArtistAlbumCountDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ArtistCustomRepository {
    Flux<ArtistAlbumCountDto> findAlbumCountByArtist(Pageable pageable);
}
