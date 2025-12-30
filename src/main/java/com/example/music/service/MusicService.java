package com.example.music.service;

import com.example.music.init.DataInitializer;
import com.example.music.repository.MusicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
@Slf4j
public class MusicService {
    private final DataInitializer dataInitializer;
    private final MusicRepository songRepository;
    private final File jsonFile;

    public MusicService(DataInitializer dataInitializer,
                        MusicRepository songRepository,
                        @Value("${resource.json.path}") String jsonPath) {
        this.dataInitializer = dataInitializer;
        this.songRepository = songRepository;
        this.jsonFile = new File(jsonPath);
    }

    public Mono<Void> syncMusicMetadata() {
        log.debug("Start sync music data");
        return dataInitializer.streamJsonToDto(jsonFile)
                .buffer(50)
                .flatMap(songRepository::bulkInsert)
                .doOnNext(Void -> log.debug("Success bulk insert data"))
                .then();
    }
}
