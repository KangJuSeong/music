package com.example.music.repository;


import com.example.music.entity.SongMoodEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongMoodRepository extends ReactiveCrudRepository<SongMoodEntity, Long>, MusicCustomRepository {
}
