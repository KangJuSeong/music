package com.example.music.sync;

import com.example.music.dto.MusicSyncContext;
import com.example.music.dto.SongJsonDto;
import com.example.music.entity.AlbumEntity;
import com.example.music.entity.ArtistEntity;
import com.example.music.init.DataInitializer;
import com.example.music.repository.*;
import com.example.music.repository.song.SongRepository;
import com.example.music.service.MusicSyncService;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MusicSyncTest {
    @Mock
    DataInitializer dataInitializer;
    @Mock
    AlbumRepository albumRepository;
    @Mock
    SongRepository songRepository;
    @Mock
    ArtistRepository artistRepository;
    @Mock
    SongFeatureRepository songFeatureRepository;
    @Mock
    SongMoodRepository songMoodRepository;
    @Mock
    ListenContextRepository listenContextRepository;
    @Mock
    ArtistAlbumRepository artistAlbumRepository;
    @Mock
    SimilarSongRepository similarSongRepository;
    private MusicSyncService musicSyncService;
    private Map<String, Long> reflectAlbumCache;
    private Map<String, Long> reflectArtistCache;
    private Set<String> reflectArtistAlbumCache;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        musicSyncService = new MusicSyncService(
                dataInitializer,
                albumRepository,
                songRepository,
                artistRepository,
                songFeatureRepository,
                songMoodRepository,
                listenContextRepository,
                artistAlbumRepository,
                similarSongRepository,
                "/test"
        );
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        // 캐시 핸들링을 위한 reflect
        reflectAlbumCache = (ConcurrentHashMap<String, Long>) ReflectionTestUtils
                .getField(musicSyncService, "albumCache");
        reflectArtistCache = (ConcurrentHashMap<String, Long>) ReflectionTestUtils
                .getField(musicSyncService, "artistCache");
        reflectArtistAlbumCache = (ConcurrentHashMap.KeySetView<String, Boolean>) ReflectionTestUtils
                .getField(musicSyncService, "artistAlbumCache");

        // 테스트 시작 시, 캐시 초기화
        reflectAlbumCache.clear();
        reflectArtistCache.clear();
        reflectArtistAlbumCache.clear();
    }

    @Test
    @DisplayName("캐시 미스 시 DB에 아티스트를 저장한다.")
    void shouldSaveToDatabaseWhenArtistCacheMiss() throws JsonProcessingException {
        // given: JSON DTO, MusicContextDto, ArtistEntity
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        MusicSyncContext ctx = new MusicSyncContext(dto);
        ArtistEntity artistEntity = dto.toArtistEntity();
        Long artistId = 1L;
        artistEntity.setArtistId(artistId);
        when(artistRepository.save(any()))
                .thenReturn(Mono.just(artistEntity));

        // when
        Mono<MusicSyncContext> result = musicSyncService.getOrSaveArtist(ctx);

        // then
        StepVerifier.create(result)
                .expectNextMatches(c -> c.getArtistId().equals(artistId))
                .verifyComplete();
        assertTrue(reflectArtistCache.containsKey(dto.getArtist()));
        assertEquals(artistId, reflectArtistCache.get(dto.getArtist()));
    }

    @Test
    @DisplayName("캐시 히트 시 DB를 호출하지 않고 아티스트 캐시 값을 사용한다.")
    void shouldReturnCacheValueWhenArtistCacheHit() throws JsonProcessingException {
        // given: JSON DTO, MusicContextDto, ArtistCache
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        MusicSyncContext ctx = new MusicSyncContext(dto);
        Long artistId = 1L;
        reflectArtistCache.put(dto.getArtist(), artistId);

        // when
        Mono<MusicSyncContext> result = musicSyncService.getOrSaveArtist(ctx);

        // then
        StepVerifier.create(result)
                .expectNextMatches(c -> c.getArtistId().equals(artistId))
                .verifyComplete();
    }

    @Test
    @DisplayName("캐시 미스 시 DB에 앨범을 저장한다.")
    void shouldSaveToDatabaseWhenAlbumCacheMiss() throws JsonProcessingException {
        // given: JSON DTO, MusicContextDto, AlbumEntity
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        MusicSyncContext ctx = new MusicSyncContext(dto);
        AlbumEntity albumEntity = dto.toAlbumEntity();
        Long albumId = 1L;
        albumEntity.setAlbumId(albumId);
        when(albumRepository.save(any()))
                .thenReturn(Mono.just(albumEntity));

        // when
        Mono<MusicSyncContext> result = musicSyncService.getOrSaveAlbum(ctx);

        // then
        StepVerifier.create(result)
                .expectNextMatches(c -> c.getAlbumId().equals(albumId))
                .verifyComplete();
        assertTrue(reflectAlbumCache.containsKey(dto.getAlbum()));
        assertEquals(albumId, reflectAlbumCache.get(dto.getAlbum()));
    }

    @Test
    @DisplayName("캐시 히트 시 DB를 호출하지 않고 앨범 캐시 값을 사용한다.")
    void shouldReturnCacheValueWhenAlbumCacheHit() throws JsonProcessingException {
        // given: JSON DTO, MusicContextDto, ArtistCache
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        MusicSyncContext ctx = new MusicSyncContext(dto);
        Long albumId = 1L;
        reflectAlbumCache.put(dto.getAlbum(), albumId);

        // when
        Mono<MusicSyncContext> result = musicSyncService.getOrSaveAlbum(ctx);

        // then
        StepVerifier.create(result)
                .expectNextMatches(c -> c.getAlbumId().equals(albumId))
                .verifyComplete();
    }
}
