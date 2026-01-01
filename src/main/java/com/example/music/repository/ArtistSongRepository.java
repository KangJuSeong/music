package com.example.music.repository;

import com.example.music.entity.ArtistSongEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArtistSongRepository extends ReactiveCrudRepository<ArtistSongEntity, Long> {
}
