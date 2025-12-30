package com.example.music.init;

import com.example.music.dto.MusicJsonDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Slf4j
public class DataInitializer {
    private final ObjectMapper objectMapper;

    public DataInitializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Flux<MusicJsonDto> streamJsonToDto(File file) {
        return Flux.using(
                () -> Files.newBufferedReader(file.toPath()),
                reader -> Flux.fromStream(reader.lines())
                        .map(line -> {
                            try {
                                return objectMapper.readValue(line, MusicJsonDto.class);
                            } catch (JsonProcessingException e) {
                                log.error("Fail JSON to DTO");
                                throw new RuntimeException(e.getMessage());
                            }
                        }),
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.error("Fail close reader");
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
    }
}
