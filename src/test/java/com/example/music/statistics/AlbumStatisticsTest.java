package com.example.music.statistics;

import com.example.music.dto.ArtistAlbumCountDto;
import com.example.music.dto.SongJsonDto;
import com.example.music.init.DataInitializer;
import com.example.music.repository.artist.ArtistRepository;
import com.example.music.service.MusicSyncService;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(MusicSyncService.class)
public class AlbumStatisticsTest {
    @MockitoBean
    DataInitializer dataInitializer;
    @Autowired
    MusicSyncService musicSyncService;
    @Autowired
    ArtistRepository artistRepository;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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
}
