package com.example.music.repository;


import com.example.music.entity.ArtistEntity;
import com.example.music.repository.song.SongCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ArtistRepository extends ReactiveCrudRepository<ArtistEntity, Long>, SongCustomRepository {
}
