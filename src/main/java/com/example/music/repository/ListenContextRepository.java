package com.example.music.repository;


import com.example.music.entity.ListenContextEntity;
import com.example.music.repository.song.SongCustomRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ListenContextRepository extends ReactiveCrudRepository<ListenContextEntity, Long>, SongCustomRepository {
}
