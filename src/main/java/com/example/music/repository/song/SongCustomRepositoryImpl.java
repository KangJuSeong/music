package com.example.music.repository.song;

import com.example.music.dto.SongJsonDto;
import com.example.music.dto.SongResponseDto;
import com.example.music.entity.AlbumEntity;
import com.example.music.entity.ArtistEntity;
import com.example.music.entity.SongEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class SongCustomRepositoryImpl implements SongCustomRepository {
    private final DatabaseClient client;

    public SongCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Flux<SongResponseDto> findAllByPageable(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().toList().getFirst();
        String sortBy = order.getProperty();
        String direction = order.getDirection().toString();
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = String.format(
                """
                SELECT s.song_id as song_id, s.song_name as song_name, s.length as length, s.genre as genre,
                s.lyrics as lyrics, s.explicit as explicit,
                ar.artist_id as artist_id, ar.artist_name as artist_name,
                al.album_id as album_id, al.album_name as album_name, al.release_date as release_date
                FROM songs s
                JOIN artists ar ON s.artist_id = ar.artist_id
                JOIN albums al ON s.album_id = al.album_id
                WHERE s.song_name IS NOT NULL
                ORDER BY %s %s
                LIMIT :limit OFFSET :offset
                """, sortBy, direction);
        return client.sql(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, metadata) -> {
                    SongEntity songEntity = SongEntity.builder()
                            .songId(row.get("song_id", Long.class))
                            .songName(row.get("song_name", String.class))
                            .length(row.get("length", String.class))
                            .genre(row.get("genre", String.class))
                            .lyrics(row.get("lyrics", String.class))
                            .explicit(row.get("explicit", Boolean.class))
                            .artistId(row.get("artist_id", Long.class))
                            .albumId(row.get("album_id", Long.class))
                            .build();
                    ArtistEntity artistEntity = ArtistEntity.builder()
                            .artistId(row.get("artist_id", Long.class))
                            .artistName(row.get("artist_name", String.class))
                            .build();
                    AlbumEntity albumEntity = AlbumEntity.builder()
                            .albumId(row.get("album_id", Long.class))
                            .albumName(row.get("album_name", String.class))
                            .releaseDate(row.get("release_date", LocalDate.class))
                            .build();
                    SongResponseDto songResponseDto = new SongResponseDto();
                    songResponseDto.setSongEntity(songEntity);
                    songResponseDto.setArtistEntity(artistEntity);
                    songResponseDto.setAlbumEntity(albumEntity);
                    return songResponseDto;
                })
                .all();
    }
}
