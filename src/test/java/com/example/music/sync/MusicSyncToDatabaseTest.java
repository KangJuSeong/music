package com.example.music.sync;

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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(MusicSyncService.class)
public class MusicSyncToDatabaseTest {
    @MockitoBean
    DataInitializer dataInitializer;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    SongRepository songRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    SongFeatureRepository songFeatureRepository;
    @Autowired
    SongMoodRepository songMoodRepository;
    @Autowired
    ListenContextRepository listenContextRepository;
    @Autowired
    ArtistAlbumRepository artistAlbumRepository;
    @Autowired
    SimilarSongRepository similarSongRepository;
    @Autowired
    MusicSyncService musicSyncService;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("JSON DTO 를 이용하여 아티스트, 앨범, 곡, 유사곡, 아티스트 - 앨범 정보를 DB에 저장한다.")
    void shouldSaveToDatabaseForMusicData() throws JsonProcessingException {
        // given: JSON DTO
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto.setArtist("Artist1, Artist2,Artist3");
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto));

        // when
        Mono<Void> result = musicSyncService.syncMusicData();

        // then
        StepVerifier.create(result).verifyComplete();
        verifyAlbum(dto.toAlbumEntity());
        verifyArtist(dto.toArtistEntity());
        verifySong(dto.toSongEntity(1L, 1L));
        verifySongFeature(dto.toSongFeatureEntity(1L));
        verifySongMood(dto.toSongMoodEntity(1L));
        verifyListenContext(dto.toListenContextEntity(1L));
        List<SimilarSongEntity> similarSongEntities = dto.getSimilarSongs().stream()
                .map(s -> s.toSimilarSongEntity(1L))
                .toList();
        verifySimilarSong(similarSongEntities);
        List<ArtistAlbumEntity> artistAlbumEntities = Arrays.stream(dto.getArtist().split(","))
                .map(String::trim)
                .map(sa -> new ArtistAlbumEntity(sa, 1L))
                .toList();
        verifyArtistAlbum(artistAlbumEntities);
    }

    private void verifyAlbum(AlbumEntity expect) {
        AlbumEntity albumEntity = albumRepository.findById(1L).block();
        assert albumEntity != null;
        assertEquals(1L, albumEntity.getAlbumId());
        Assertions.assertThat(albumEntity)
                .usingRecursiveComparison()
                .ignoringFields("albumId")
                .isEqualTo(expect);
    }

    private void verifyArtist(ArtistEntity expect) {
        ArtistEntity artistEntity = artistRepository.findById(1L).block();
        assert artistEntity != null;
        assertEquals(1L, artistEntity.getArtistId());
        Assertions.assertThat(artistEntity)
                .usingRecursiveComparison()
                .ignoringFields("artistId")
                .isEqualTo(expect);
    }

    private void verifySong(SongEntity expect) {
        SongEntity songEntity = songRepository.findById(1L).block();
        assert songEntity != null;
        assertEquals(1L, songEntity.getSongId());
        assertEquals(1L, songEntity.getArtistId());
        assertEquals(1L, songEntity.getAlbumId());
        Assertions.assertThat(songEntity)
                .usingRecursiveComparison()
                .ignoringFields("songId", "artistId", "albumId")
                .isEqualTo(expect);
    }

    private void verifySongFeature(SongFeatureEntity expect) {
        SongFeatureEntity songFeatureEntity = songFeatureRepository.findById(1L).block();
        assert songFeatureEntity != null;
        assertEquals(1L, songFeatureEntity.getFeatureId());
        assertEquals(1L, songFeatureEntity.getSongId());
        Assertions.assertThat(songFeatureEntity)
                .usingRecursiveComparison()
                .ignoringFields("featureId", "songId")
                .isEqualTo(expect);
    }

    private void verifySongMood(SongMoodEntity expect) {
        SongMoodEntity songMoodEntity = songMoodRepository.findById(1L).block();
        assert songMoodEntity != null;
        assertEquals(1L, songMoodEntity.getMoodId());
        assertEquals(1L, songMoodEntity.getSongId());
        Assertions.assertThat(songMoodEntity)
                .usingRecursiveComparison()
                .ignoringFields("moodId", "songId")
                .isEqualTo(expect);
    }

    private void verifyListenContext(ListenContextEntity expect) {
        ListenContextEntity listenContextEntity = listenContextRepository.findById(1L).block();
        assert listenContextEntity != null;
        assertEquals(1L, listenContextEntity.getContextId());
        assertEquals(1L, listenContextEntity.getSongId());
        Assertions.assertThat(listenContextEntity)
                .usingRecursiveComparison()
                .ignoringFields("contextId", "songId")
                .isEqualTo(expect);
    }

    private void verifySimilarSong(List<SimilarSongEntity> expects) {
        List<SimilarSongEntity> similarSongEntities = similarSongRepository.findAll().collectList().block();
        assert similarSongEntities != null;
        AtomicLong id = new AtomicLong(1L);
        similarSongEntities.forEach(similar -> {
            assertEquals(id.getAndAdd(1L), similar.getSimilarId());
            assertEquals(1L, similar.getSongId());
        });
        Assertions.assertThat(similarSongEntities)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("similarId", "songId")
                .containsAll(expects);
    }

    private void verifyArtistAlbum(List<ArtistAlbumEntity> expects) {
        List<ArtistAlbumEntity> artistAlbumEntities = artistAlbumRepository.findAll().collectList().block();
        assert artistAlbumEntities != null;
        Assertions.assertThat(artistAlbumEntities)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expects);
    }
}
