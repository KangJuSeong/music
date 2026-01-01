package com.example.music.repository;

import com.example.music.entity.ArtistAlbumEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArtistAlbumRepository extends ReactiveCrudRepository<ArtistAlbumEntity, Long> {
}
