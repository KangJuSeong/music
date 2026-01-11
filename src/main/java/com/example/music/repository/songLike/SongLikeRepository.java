package com.example.music.repository.songLike;

import com.example.music.entity.SongLikeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SongLikeRepository extends ReactiveCrudRepository<SongLikeEntity, Long>, SongLikeCustomRepository {

}
