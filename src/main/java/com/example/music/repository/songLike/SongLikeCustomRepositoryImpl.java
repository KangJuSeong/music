package com.example.music.repository.songLike;

import com.example.music.dto.SongLikeDto;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

public class SongLikeCustomRepositoryImpl implements SongLikeCustomRepository {
    private final DatabaseClient client;

    public SongLikeCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Flux<SongLikeDto> findTopLikeCount(Integer top) {
        String sql = String.format(
                """
                SELECT s.song_name as song_name, COUNT(sl.song_like_id) as likes
                FROM song_likes sl
                JOIN songs s
                ON sl.song_id = s.song_id
                WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
                GROUP BY sl.song_id
                ORDER BY likes DESC
                LIMIT %d
                """, top);
        return client.sql(sql)
                .map((row, metadata) -> new SongLikeDto((String) row.get("song_name"), (Long) row.get("likes")))
                .all();
    }
}
