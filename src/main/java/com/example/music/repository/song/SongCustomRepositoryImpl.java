package com.example.music.repository.song;

import com.example.music.dto.SongJsonDto;
import com.example.music.entity.SongEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class SongCustomRepositoryImpl implements SongCustomRepository {
    private final DatabaseClient client;

    public SongCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Flux<SongEntity> bulkInsert(List<SongEntity> songEntities) {
        return null;
    }
}
