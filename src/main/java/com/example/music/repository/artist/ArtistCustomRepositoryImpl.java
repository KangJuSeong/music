package com.example.music.repository.artist;

import com.example.music.dto.ArtistAlbumCountDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

public class ArtistCustomRepositoryImpl implements ArtistCustomRepository {
    private final DatabaseClient client;

    public ArtistCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Flux<ArtistAlbumCountDto> findAlbumCountByArtist(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().toList().getFirst();
        String sortBy = order.getProperty();
        String direction = order.getDirection().toString();
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = String.format(
                """
                SELECT a.artist_name, COUNT(DISTINCT album_id) as album_count
                FROM artists a
                JOIN artists_albums aa
                ON a.artist_id = aa.artist_id
                GROUP BY a.artist_id
                ORDER BY %s %s
                LIMIT :limit OFFSET :offset
                """
        , sortBy, direction);
        return client.sql(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, metadata) ->
                        new ArtistAlbumCountDto(
                                row.get("artist_name", String.class),
                                row.get("album_count", Long.class)
                        )
                )
                .all();
    }
}
