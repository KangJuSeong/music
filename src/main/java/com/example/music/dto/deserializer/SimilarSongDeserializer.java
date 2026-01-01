package com.example.music.dto.deserializer;

import com.example.music.dto.SimilarSongJsonDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;

public class SimilarSongDeserializer extends JsonDeserializer<SimilarSongJsonDto> {
    @Override
    public SimilarSongJsonDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        SimilarSongJsonDto similarSong = new SimilarSongJsonDto();
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.contains("Similar Artist")) {
                String value = node.get(key).textValue();
                similarSong.setSimilarArtist(value);
            } else if (key.contains("Similar Song")) {
                String value = node.get(key).textValue();
                similarSong.setSimilarSong(value);
            } else if (key.contains("Similarity Score")) {
                double value = node.get(key).asDouble();
                similarSong.setSimilarScore(value);
            } else {
                throw new IllegalArgumentException("No contains key artis, song, score");
            }
        }
        return similarSong;
    }
}
