package com.example.music.deserializer;

import com.example.music.dto.SongJsonDto;
import com.example.music.dto.SimilarSongJsonDto;
import com.example.music.init.DataInitializer;
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
    public final static String json =
            """
                {
                    "Artist(s)": "!!!",
                    "song": "Even When the Waters Cold",
                    "text": "Friends told her",
                    "Length": "03:47",
                    "emotion": "sadness",
                    "Genre": "hip hop",
                    "Album": "Thr!!!er",
                    "Release Date": "2013-04-29",
                    "Key": "D min",
                    "Tempo": 0.4378698225,
                    "Loudness (db)": 0.785065407,
                    "Time signature": "4\\/4",
                    "Explicit": "No",
                    "Popularity": "40",
                    "Energy": "83",
                    "Danceability": "71",
                    "Positiveness": "87",
                    "Speechiness": "4",
                    "Liveness": "16",
                    "Acousticness": "11",
                    "Instrumentalness": "0",
                    "Good for Party": 0,
                    "Good for Work\\/Study": 0,
                    "Good for Relaxation\\/Meditation": 0,
                    "Good for Exercise": 0,
                    "Good for Running": 0,
                    "Good for Yoga\\/Stretching": 0,
                    "Good for Driving": 0,
                    "Good for Social Gatherings": 0,
                    "Good for Morning Routine": 0,
                    "Similar Songs": [
                        {
                            "Similar Artist 1": "Corey Smith",
                            "Similar Song 1": "If I Could Do It Again",
                            "Similarity Score": 0.9860607848
                        },
                        {
                            "Similar Artist 2": "Toby Keith",
                            "Similar Song 2": "Drinks After Work",
                            "Similarity Score": 0.9837194774
                        },
                        {
                            "Similar Artist 3": "Space",
                            "Similar Song 3": "Neighbourhood",
                            "Similarity Score": 0.9832363508
                        }
                    ]
                }
                """;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("JSON 데이터를 DTO로 정상적으로 변환되어야 한다")
    void shouldReturnMusicJsonDtoTest() throws JsonProcessingException {
        // when
        SongJsonDto dto = objectMapper.readValue(json, SongJsonDto.class);

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
        String ndjson = json.replace("\n", "").replace("\r", "");
        String ndjsons = ndjson + "\n" + ndjson.replace("!!!", "modify");
        DataInitializer dataInitializer = new DataInitializer();
        File testFile = File.createTempFile("test", ".json");
        Files.writeString(testFile.toPath(), ndjsons, StandardOpenOption.WRITE);

        // when
        Flux<SongJsonDto> flux = dataInitializer.streamJsonToDto(testFile);

        // then
        StepVerifier.create(flux)
                .expectNextMatches(dto -> dto.getArtist().equals("!!!"))
                .expectNextMatches(dto -> dto.getArtist().equals("modify"))
                .verifyComplete();
    }
}
