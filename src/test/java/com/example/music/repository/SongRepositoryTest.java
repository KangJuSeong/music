package com.example.music.repository;

import com.example.music.deserializer.MusicDeserializerTest;
import com.example.music.dto.MusicJsonDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
@DataR2dbcTest
@ActiveProfiles("test")
public class SongRepositoryTest {
    @Autowired
    private DatabaseClient client;

    private MusicCustomRepositoryImpl songRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        songRepository = new MusicCustomRepositoryImpl(client);
    }

    @Test
    @DisplayName("DTO로 변환한 JSON 데이터에 대한 bulk insert를 테스트한다.")
    void shouldSuccessBulkInsert() throws JsonProcessingException {
        // given
        MusicJsonDto dto = objectMapper.readValue(MusicDeserializerTest.json, MusicJsonDto.class);
        List<MusicJsonDto> dtoList = List.of(dto);

        // when
        Mono<Void> mono = songRepository.bulkInsert(dtoList);

        // then
        StepVerifier.create(mono).verifyComplete();
    }
}
