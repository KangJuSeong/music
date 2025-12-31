package com.example.music.repository;


import com.example.music.entity.SongFeatureEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongFeatureRepository extends ReactiveCrudRepository<SongFeatureEntity, Long>, MusicCustomRepository {
}
