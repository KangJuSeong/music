package com.example.music.repository;


import com.example.music.entity.SimilarSongEntity;
import com.example.music.repository.song.SongCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SimilarSongRepository extends ReactiveCrudRepository<SimilarSongEntity, Long>, SongCustomRepository {
}
