package com.example.music.sync;

import com.example.music.dto.MusicSyncContext;
import com.example.music.dto.SongJsonDto;
import com.example.music.entity.*;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
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

    @Test
    @DisplayName("캐시 히트 시 DB에 아티스트-앨범을 INSERT 하지 않는다.")
    void shouldNotInsertToDatabaseForArtistAlbumWhenCacheHit() throws JsonProcessingException {
        // given: JSON DTO, MusicContextDto, Album ID, Artist Cache, ArgumentCaptor<List<ArtistEntity>> 
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        Long albumId = 1L;
        MusicSyncContext ctx = new MusicSyncContext(dto);
        ctx.setAlbumId(albumId);
        ArgumentCaptor<List<ArtistAlbumEntity>> artistAlbumCaptor = ArgumentCaptor.forClass(List.class);
        Arrays.stream(dto.getArtist().split(",")).forEach(sa -> reflectArtistAlbumCache.add(sa + "|" + albumId.toString()));
        when(artistAlbumRepository.saveAll(anyList()))
                .thenReturn(Flux.empty());

        // when
        Flux<ArtistAlbumEntity> result = musicSyncService.saveArtistsAlbums(ctx);

        // then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
        verify(artistAlbumRepository).saveAll(artistAlbumCaptor.capture());
        assertEquals(artistAlbumCaptor.getValue().size(), 0);
    }

    @Test
    @DisplayName("캐시 미스 시 DB에 아티스트-앨범을 INSERT 한다.")
    void shouldInsertToDatabaseAndCacheForArtistAlbumWhenCacheMiss() throws JsonProcessingException {
        // given: JSON DTO, Album ID, MusicSyncContext, ArgumentCaptor<List<ArtistAlbumEntity>>, List<ArtistAlubmEntity>> 
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto.setArtist("Artist1, Artist2 , Artist3");
        Long albumId = 1L;
        MusicSyncContext ctx = new MusicSyncContext(dto);
        ctx.setAlbumId(albumId);
        ArgumentCaptor<List<ArtistAlbumEntity>> artistAlbumCaptor = ArgumentCaptor.forClass(List.class);
        List<ArtistAlbumEntity> artistAlbumEntities = Arrays.stream(dto.getArtist().split(","))
                .map(String::trim)
                .map(sa -> new ArtistAlbumEntity(sa, albumId))
                .toList();
        when(artistAlbumRepository.saveAll(anyList())).thenReturn(Flux.fromIterable(artistAlbumEntities));

        // when
        Flux<ArtistAlbumEntity> result = musicSyncService.saveArtistsAlbums(ctx);

        // then
        StepVerifier.create(result)
                .expectNextCount(artistAlbumEntities.size())
                .verifyComplete();
        verify(artistAlbumRepository).saveAll(artistAlbumCaptor.capture());
        List<ArtistAlbumEntity> filterdArtistAlbumEntities = artistAlbumCaptor.getValue();
        assertEquals(artistAlbumEntities.stream().map(ArtistAlbumEntity::getAlbumId).toList(), 
                     filterdArtistAlbumEntities.stream().map(ArtistAlbumEntity::getAlbumId).toList());
        assertEquals(artistAlbumEntities.stream().map(ArtistAlbumEntity::getSplitArtistName).toList(), 
                     filterdArtistAlbumEntities.stream().map(ArtistAlbumEntity::getSplitArtistName).toList());
        artistAlbumEntities.forEach(entity -> {
            String key = entity.getSplitArtistName() + "|" + entity.getAlbumId().toString();
            assertFalse(reflectArtistAlbumCache.add(key));
        });
    }

    @Test
    @DisplayName("유사곡 목록에서 유사곡명 또는 유사곡 아티스트가 NULL인 데이터를 필터링 한다.")
    void shouldExcludeInsertNullDataForArtistOrSong() throws JsonProcessingException {
        // given: JSON DTO, MusicSyncContext, Song ID 
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        Long songId = 1L;
        MusicSyncContext ctx = new MusicSyncContext(dto);
        // 첫번째 유사곡은 아티스트 null, 두번째 유사곡은 곡명 null 로 저장
        dto.getSimilarSongs().get(0).setSimilarArtist(null);
        dto.getSimilarSongs().get(1).setSimilarSong(null);
        ctx.setSongId(songId);
        when(similarSongRepository.saveAll(anyList())).thenReturn(Flux.empty());
        ArgumentCaptor<List<SimilarSongEntity>> captor = ArgumentCaptor.forClass(List.class);

        // when
        Flux<SimilarSongEntity> result = musicSyncService.saveSimilarSongs(ctx);

        // then
        StepVerifier.create(result)
                .verifyComplete();
        verify(similarSongRepository).saveAll(captor.capture());
        List<SimilarSongEntity> filterdSimilarSongs = captor.getValue();
        assertEquals(1, filterdSimilarSongs.size());
        SimilarSongEntity filteredSimilarSong  = filterdSimilarSongs.getFirst();
        assertEquals(dto.getSimilarSongs().getLast().getSimilarSong(), filteredSimilarSong.getSimilarSong());
        assertEquals(dto.getSimilarSongs().getLast().getSimilarArtist(), filteredSimilarSong.getSimilarArtist());
        assertEquals(dto.getSimilarSongs().getLast().getSimilarScore(), filteredSimilarSong.getSimilarScore());
    }
}
