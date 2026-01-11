package com.example.music.statistics;

import com.example.music.dto.ArtistAlbumCountDto;
import com.example.music.dto.SongJsonDto;
import com.example.music.dto.YearlyAlbumCountDto;
import com.example.music.init.DataInitializer;
import com.example.music.repository.album.AlbumRepository;
import com.example.music.repository.artist.ArtistRepository;
import com.example.music.service.AlbumStatisticsService;
import com.example.music.service.MusicSyncService;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@ActiveProfiles("test")
@Import({MusicSyncService.class, AlbumStatisticsService.class})
public class AlbumStatisticsTest {
    @MockitoBean
    DataInitializer dataInitializer;
    @Autowired
    MusicSyncService musicSyncService;
    @Autowired
    AlbumStatisticsService albumStatisticsService;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    AlbumRepository albumRepository;
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
    @DisplayName("연도별 앨범 수를 조회한다.")
    void shouldReturnYearlyAlbumCounts() throws JsonProcessingException {
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setAlbum("Another album");
        dto2.setReleaseDate(LocalDate.of(2030, 12, 20));
        Pageable pageable = PageRequest.of(0, 2, Sort.by("release_date").descending());
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2));
        musicSyncService.syncMusicData().block();

        // when
        Flux<YearlyAlbumCountDto> result = albumRepository.findAlbumCountsByYearly(pageable);

        // then
        StepVerifier.create(result)
                .expectNextMatches(count1 -> count1.getAlbumCount() == 1L && "2030".equals(count1.getReleaseYear()))
                .expectNextMatches(count2 -> count2.getAlbumCount() == 1L && "2013".equals(count2.getReleaseYear()))
                .verifyComplete();

    }

    @Test
    @DisplayName("전체 연도 수를 조회한다.")
    void shouldReturnYearCount() throws JsonProcessingException {
        // given
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setAlbum("Another album");
        dto2.setReleaseDate(LocalDate.of(2030, 12, 20));
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2));
        musicSyncService.syncMusicData().block();

        // when
        Mono<Long> result = albumRepository.countDistinctYearForAlbums();

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("전체 연도와 연도별 앨범 수를 조합하여 응답 결과를 만든다.")
    void shouldReturnPageOfYearlyAlbumCount() throws JsonProcessingException {
        // given
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto3 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setAlbum("Another album1");
        dto2.setReleaseDate(LocalDate.of(2030, 12, 20));
        dto3.setAlbum("Another album2");
        dto3.setReleaseDate(LocalDate.of(2030, 11, 11));
        Pageable pageable = PageRequest.of(0, 2, Sort.by("release_year").descending());
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2, dto3));
        musicSyncService.syncMusicData().block();

        // when
        Mono<Page<YearlyAlbumCountDto>> result = albumStatisticsService.getAlbumsCountByYearPageable(pageable);

        // then
        StepVerifier.create(result)
                .expectNextMatches(p -> {
                    YearlyAlbumCountDto countDto1 = p.getContent().getFirst();
                    YearlyAlbumCountDto countDto2 = p.getContent().getLast();
                    return countDto1.getAlbumCount() == 2L &&
                            "2030".equals(countDto1.getReleaseYear()) &&
                            countDto2.getAlbumCount() == 1L &&
                            "2013".equals(countDto2.getReleaseYear());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("아티스트 별 앨범 수를 조회한다.")
    void shouldReturnArtistAlbumCounts() throws JsonProcessingException {
        // given
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setAlbum("Another album");
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2));
        musicSyncService.syncMusicData().block();
        Pageable pageable = PageRequest.of(0, 2, Sort.by("artist_name").descending());

        // when
        Flux<ArtistAlbumCountDto> result = artistRepository.findAlbumCountByArtist(pageable);

        // then
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getAlbumCount() == 2 && dto.getArtistName().equals(dto1.getArtist()))
                .verifyComplete();
    }

    @Test
    @DisplayName("전체 아티스트의 수를 조회한다")
    void shouldReturnArtistCount() throws JsonProcessingException {
        // given
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setArtist("Another artist");
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2));
        musicSyncService.syncMusicData().block();

        // when
        Mono<Long> result = artistRepository.countDistinctArtistForAlbums();

        // then
        StepVerifier.create(result)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    @DisplayName("전체 아티스트의 수와 아티스트 별 앨범 수를 조합하여 응답 결과를 만든다.")
    void shouldReturnPageOfArtistAlbumCount() throws JsonProcessingException {
        // given
        SongJsonDto dto1 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        SongJsonDto dto2 = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);
        dto2.setAlbum("Another album");
        dto2.setArtist("Another artist");
        Pageable pageable = PageRequest.of(0, 2, Sort.by("artist_name").ascending());
        when(dataInitializer.streamJsonToDto(any())).thenReturn(Flux.just(dto1, dto2));
        musicSyncService.syncMusicData().block();

        // when
        Mono<Page<ArtistAlbumCountDto>> result = albumStatisticsService.getAlbumsCountByArtistPageable(pageable);

        // then
        StepVerifier.create(result)
                .expectNextMatches(p -> {
                    ArtistAlbumCountDto countDto1 = p.getContent().getFirst();
                    ArtistAlbumCountDto countDto2 = p.getContent().getLast();
                    return countDto1.getArtistName().equals(dto1.getArtist()) &&
                            countDto1.getAlbumCount() == 1L &&
                            countDto2.getArtistName().equals(dto2.getArtist()) &&
                            countDto2.getAlbumCount() == 1L;
                })
                .verifyComplete();
    }
}
