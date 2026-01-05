package com.example.music.repository;


import com.example.music.entity.SongMoodEntity;
import com.example.music.repository.song.SongCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongMoodRepository extends ReactiveCrudRepository<SongMoodEntity, Long> {
}
