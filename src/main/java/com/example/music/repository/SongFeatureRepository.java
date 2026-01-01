package com.example.music.repository;


import com.example.music.entity.SongFeatureEntity;
import com.example.music.repository.song.SongCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongFeatureRepository extends ReactiveCrudRepository<SongFeatureEntity, Long>, SongCustomRepository {
}
