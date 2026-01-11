package com.example.music.statistics;

import com.example.music.dto.SongJsonDto;
import com.example.music.dto.SongLikeDto;
import com.example.music.entity.SongEntity;
import com.example.music.entity.SongLikeEntity;
import com.example.music.init.DataInitializer;
import com.example.music.repository.song.SongRepository;
import com.example.music.repository.songLike.SongLikeRepository;
import com.example.music.service.MusicSyncService;
import com.example.music.service.SongLikeService;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@ActiveProfiles("test")
@Import({SongLikeService.class, MusicSyncService.class})
@Slf4j
public class SongLikeStatisticsTest {
    @MockitoBean
    DataInitializer dataInitializer;
    @Autowired
    MusicSyncService musicSyncService;
    @Autowired
    SongLikeService songLikeService;
    @Autowired
    SongRepository songRepository;
    @Autowired
    SongLikeRepository songLikeRepository;
    @Autowired
    DatabaseClient client;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    void tearDown() {
        client.sql("DELETE FROM albums").fetch().rowsUpdated().block();
        client.sql("DELETE FROM artists").fetch().rowsUpdated().block();
    }

    @Test
    @DisplayName("좋아요 증가 TOP 10을 조회한다.")
    void shouldReturnIncreaseLikeTop10Songs() throws JsonProcessingException {
        // given: 11개 곡 추가
        List<SongJsonDto> songDataList = new ArrayList<>();
        int dataSize = 11;
        for (int i=0; i<dataSize; i++) {
            String songName = "Test Song Name - %d".formatted(i);
            SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
            dto.setSong(songName);
            songDataList.add(dto);
        }
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.fromIterable(songDataList));
        musicSyncService.syncMusicData().block();
        List<Long> songIds = Objects.requireNonNull(songRepository.findAll().collectList().block())
                .stream()
                .map(SongEntity::getSongId)
                .toList();
        Map<Long, Long> songLikeCounter = new HashMap<>();

        // when: 무작위로 곡 좋아요
        for (Long songId : songIds) {
            int likeCount = ThreadLocalRandom.current().nextInt(1, 101);
            for (int j=0; j<likeCount; j++) {
                songLikeService.insertSongLike(songId).block();
            }
            songLikeCounter.put(songId, (long) likeCount);
        }
        Flux<SongLikeDto> result = songLikeService.getSongLikeIncreaseTop(10);

        // then
        StepVerifier.create(result)
                .recordWith(ArrayList::new)
                .expectNextCount(10L)
                .consumeRecordedWith(list -> {
                    List<SongLikeDto> results = new ArrayList<>(list);
                    assertThat(results).isSortedAccordingTo(
                        Comparator.comparing(SongLikeDto::getLikes).reversed()
                    );

                    results.forEach(sl -> {
                        assertEquals(songLikeCounter.get(sl.getSongId()), sl.getLikes());
                        log.debug("result -> {}", sl);
                    });
                })
                .verifyComplete();
    }


    @Test
    @DisplayName("1시간 이내 좋아요 증가 TOP 10 조회한다.")
    void shouldReturnIncreaseLikeTop10SongsFor1Hours() throws JsonProcessingException {
        // given: 10개 곡 추가
        List<SongJsonDto> songDataList = new ArrayList<>();
        int dataSize = 10;
        for (int i=0; i<dataSize; i++) {
            String songName = "Test Song Name - %d".formatted(i);
            SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
            dto.setSong(songName);
            songDataList.add(dto);
        }
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.fromIterable(songDataList));
        musicSyncService.syncMusicData().block();
        List<Long> songIds = Objects.requireNonNull(songRepository.findAll().collectList().block())
                .stream()
                .map(SongEntity::getSongId)
                .toList();
        Map<Long, Long> songLikeCounter = new HashMap<>();
        Map<Long, Boolean> songLikeIsBefore1Hour = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        // when: 무작위로 곡 좋아요 (1시간 이후 포함)
        for (Long songId : songIds) {
            int likeCount = ThreadLocalRandom.current().nextInt(1, 101);
            boolean is1HourBefore = ThreadLocalRandom.current().nextBoolean();
            LocalDateTime insertTime = is1HourBefore ? now.minusMinutes(65) : now;
            for (int j=0; j<likeCount; j++) {
                SongLikeEntity songLikeEntity = new SongLikeEntity(songId, insertTime);
                songLikeRepository.save(songLikeEntity).block();
            }
            songLikeCounter.put(songId, (long) likeCount);
            songLikeIsBefore1Hour.put(songId, is1HourBefore);
        }
        Flux<SongLikeDto> result = songLikeService.getSongLikeIncreaseTop(10);

        // then
        StepVerifier.create(result)
                .recordWith(ArrayList::new)
                .expectNextCount(songLikeIsBefore1Hour.values().stream().filter(v -> !v).count())
                .consumeRecordedWith(list -> {
                    List<SongLikeDto> results = new ArrayList<>(list);
                    assertThat(results).isSortedAccordingTo(
                            Comparator.comparing(SongLikeDto::getLikes).reversed()
                    );

                    results.forEach(sl -> {
                        assertEquals(songLikeCounter.get(sl.getSongId()), sl.getLikes());
                        assertFalse(songLikeIsBefore1Hour.get(sl.getSongId()));
                        log.debug("result -> {}", sl);
                    });
                })
                .verifyComplete();
    }
}
