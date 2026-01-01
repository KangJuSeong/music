package com.example.music.deserializer;

import com.example.music.dto.SongJsonDto;
import com.example.music.dto.SimilarSongJsonDto;
import com.example.music.init.DataInitializer;
import com.example.music.statics.StaticTestDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MusicDeserializerTest {
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("JSON 데이터를 DTO로 정상적으로 변환되어야 한다")
    void shouldReturnMusicJsonDtoTest() throws JsonProcessingException {
        // when
        SongJsonDto dto = objectMapper.readValue(StaticTestDataRepository.testJson, SongJsonDto.class);

        // then
        assertEquals("!!!", dto.getArtist());
        assertEquals("Even When the Waters Cold", dto.getSong());
        assertEquals("Friends told her", dto.getLyrics());
        assertEquals("03:47", dto.getLength());
        assertEquals("sadness", dto.getEmotion());
        assertEquals("hip hop", dto.getGenre());
        assertEquals("Thr!!!er", dto.getAlbum());
        assertEquals(LocalDate.of(2013, 4, 29), dto.getReleaseDate());
        assertEquals("D min", dto.getKeySignature());
        assertEquals(0.4378698225, dto.getTempo());
        assertEquals(0.785065407, dto.getLoudness());
        assertEquals("4/4", dto.getTimeSignature());
        assertFalse(dto.isExplicit());

        assertEquals(40, dto.getPopularity());
        assertEquals(83, dto.getEnergy());
        assertEquals(71, dto.getDanceability());
        assertEquals(87, dto.getPositiveness());
        assertEquals(4, dto.getSpeechiness());
        assertEquals(16, dto.getLiveness());
        assertEquals(11, dto.getAcousticness());
        assertEquals(0, dto.getInstrumentalness());

        assertFalse(dto.isParty());
        assertFalse(dto.isWorkOrStudy());
        assertFalse(dto.isRelaxationOrMeditation());
        assertFalse(dto.isExercise());
        assertFalse(dto.isRunning());
        assertFalse(dto.isYogaOrStretching());
        assertFalse(dto.isDriving());
        assertFalse(dto.isSocialGatherings());
        assertFalse(dto.isMorningRoutine());

        SimilarSongJsonDto firstSimilarSong = dto.getSimilarSongs().getFirst();
        assertEquals("Corey Smith", firstSimilarSong.getSimilarArtist());
        assertEquals("If I Could Do It Again", firstSimilarSong.getSimilarSong());
        assertEquals(0.9860607848, firstSimilarSong.getSimilarScore());

        SimilarSongJsonDto secondSimilarSong = dto.getSimilarSongs().get(1);
        assertEquals("Toby Keith", secondSimilarSong.getSimilarArtist());
        assertEquals("Drinks After Work", secondSimilarSong.getSimilarSong());
        assertEquals(0.9837194774, secondSimilarSong.getSimilarScore());

        SimilarSongJsonDto thirdSimilarSong = dto.getSimilarSongs().getLast();
        assertEquals("Space", thirdSimilarSong.getSimilarArtist());
        assertEquals("Neighbourhood", thirdSimilarSong.getSimilarSong());
        assertEquals(0.9832363508, thirdSimilarSong.getSimilarScore());
    }

    @Test
    @DisplayName("JSON 데이터를 읽고 MusicJsonDto로 변환하여 Flux로 반환한다")
    void shouldReturnMusicJsonDtoFlux() throws IOException {
        // given
        String json = StaticTestDataRepository.testJson;
        String ndjson = json + "\n" + json.replace("!!!", "modify");
        DataInitializer dataInitializer = new DataInitializer();
        File testFile = File.createTempFile("test", ".json");
        Files.writeString(testFile.toPath(), ndjson, StandardOpenOption.WRITE);

        // when
        Flux<SongJsonDto> flux = dataInitializer.streamJsonToDto(testFile);

        // then
        StepVerifier.create(flux)
                .expectNextMatches(dto -> dto.getArtist().equals("!!!"))
                .expectNextMatches(dto -> dto.getArtist().equals("modify"))
                .verifyComplete();
    }
}
