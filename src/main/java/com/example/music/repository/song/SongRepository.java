package com.example.music.repository.song;


import com.example.music.entity.SongEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongRepository extends ReactiveCrudRepository<SongEntity, Long>, SongCustomRepository {
}
