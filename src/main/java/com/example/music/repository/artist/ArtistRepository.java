package com.example.music.repository.artist;


import com.example.music.entity.ArtistEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArtistRepository extends ReactiveCrudRepository<ArtistEntity, Long>, ArtistCustomRepository {
}
