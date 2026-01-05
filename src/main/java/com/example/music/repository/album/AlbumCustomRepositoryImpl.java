package com.example.music.repository.album;

import com.example.music.dto.YearlyAlbumCountDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Repository
public class AlbumCustomRepositoryImpl implements AlbumCustomRepository {
    private final DatabaseClient client;

    public AlbumCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Flux<YearlyAlbumCountDto> findAlbumCountsByYearly(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().toList().getFirst();
        String sortBy = order.getProperty();
        String direction = order.getDirection().toString();
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = String.format(
                """
                SELECT release_year, COUNT(*) AS album_count
                FROM albums
                GROUP BY release_year
                HAVING release_year IS NOT NULL
                ORDER BY %s %s
                LIMIT :limit OFFSET :offset
                """,
                sortBy, direction);
        return client.sql(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, metadata) ->
                        new YearlyAlbumCountDto(
                                row.get("release_year", String.class),
                                row.get("album_count", Long.class)
                        )
                )
                .all();
    }
}
