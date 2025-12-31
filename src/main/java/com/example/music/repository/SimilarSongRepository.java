package com.example.music.repository;


import com.example.music.entity.SimilarSongEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SimilarSongRepository extends ReactiveCrudRepository<SimilarSongEntity, Long>, MusicCustomRepository {
}
