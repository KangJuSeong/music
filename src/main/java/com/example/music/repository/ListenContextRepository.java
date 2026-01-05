package com.example.music.repository;


import com.example.music.entity.ListenContextEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ListenContextRepository extends ReactiveCrudRepository<ListenContextEntity, Long> {
}
