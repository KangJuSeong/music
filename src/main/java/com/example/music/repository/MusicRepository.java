package com.example.music.repository;

import com.example.music.dto.SongEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MusicRepository extends ReactiveCrudRepository<SongEntity, Long>, MusicCustomRepository {
}
