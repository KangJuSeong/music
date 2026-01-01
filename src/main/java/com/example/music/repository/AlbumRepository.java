package com.example.music.repository;


import com.example.music.entity.AlbumEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AlbumRepository extends ReactiveCrudRepository<AlbumEntity, Long> {
}
