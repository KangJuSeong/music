package com.example.music.repository.song;

import com.example.music.entity.SongEntity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SongCustomRepository {
    Flux<SongEntity> bulkInsert(List<SongEntity> songEntities);
}
