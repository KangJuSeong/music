package com.example.music.sync;

import com.example.music.dto.SongJsonDto;
import com.example.music.entity.*;
import com.example.music.init.DataInitializer;
import com.example.music.repository.*;
import com.example.music.repository.album.AlbumRepository;
import com.example.music.repository.artist.ArtistRepository;
import com.example.music.repository.song.SongRepository;
import com.example.music.service.MusicSyncService;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
        Long albumId = verifyAlbum(dto.toAlbumEntity());
        Long artistId = verifyArtist(dto.toArtistEntity());
        Long songId = verifySong(dto.toSongEntity(albumId, artistId));
        verifySongFeature(dto.toSongFeatureEntity(songId));
        verifySongMood(dto.toSongMoodEntity(songId));
        verifyListenContext(dto.toListenContextEntity(songId));
        List<SimilarSongEntity> similarSongEntities = dto.getSimilarSongs().stream()
                .map(s -> s.toSimilarSongEntity(songId))
                .toList();
        verifySimilarSong(similarSongEntities);
        verifyArtistAlbum(new ArtistAlbumEntity(artistId, albumId));
    }

    private Long verifyAlbum(AlbumEntity expect) {
        AlbumEntity albumEntity = albumRepository.findAll().blockFirst();
        assert albumEntity != null;
        Assertions.assertThat(albumEntity)
                .usingRecursiveComparison()
                .ignoringFields("albumId")
                .isEqualTo(expect);
        return albumEntity.getAlbumId();
    }

    private Long verifyArtist(ArtistEntity expect) {
        ArtistEntity artistEntity = artistRepository.findAll().blockFirst();
        assert artistEntity != null;
        Assertions.assertThat(artistEntity)
                .usingRecursiveComparison()
                .ignoringFields("artistId")
                .isEqualTo(expect);
        return artistEntity.getArtistId();
    }

    private Long verifySong(SongEntity expect) {
        SongEntity songEntity = songRepository.findAll().blockFirst();
        assert songEntity != null;
        Assertions.assertThat(songEntity)
                .usingRecursiveComparison()
                .ignoringFields("songId")
                .isEqualTo(expect);
        return songEntity.getSongId();
    }

    private void verifySongFeature(SongFeatureEntity expect) {
        SongFeatureEntity songFeatureEntity = songFeatureRepository.findAll().blockFirst();
        assert songFeatureEntity != null;
        assertEquals(1L, songFeatureEntity.getSongId());
        Assertions.assertThat(songFeatureEntity)
                .usingRecursiveComparison()
                .ignoringFields("featureId")
                .isEqualTo(expect);
    }

    private void verifySongMood(SongMoodEntity expect) {
        SongMoodEntity songMoodEntity = songMoodRepository.findAll().blockFirst();
        assert songMoodEntity != null;
        assertEquals(1L, songMoodEntity.getSongId());
        Assertions.assertThat(songMoodEntity)
                .usingRecursiveComparison()
                .ignoringFields("moodId")
                .isEqualTo(expect);
    }

    private void verifyListenContext(ListenContextEntity expect) {
        ListenContextEntity listenContextEntity = listenContextRepository.findAll().blockFirst();
        assert listenContextEntity != null;
        Assertions.assertThat(listenContextEntity)
                .usingRecursiveComparison()
                .ignoringFields("contextId")
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
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("similarId")
                .containsAll(expects);
    }

    private void verifyArtistAlbum(ArtistAlbumEntity expect) {
        ArtistAlbumEntity artistAlbumEntities = artistAlbumRepository.findAll().blockFirst();
        assert artistAlbumEntities != null;
        Assertions.assertThat(artistAlbumEntities)
                .usingRecursiveComparison()
                .isEqualTo(expect);
    }
}
