package com.example.music.repository.album;


import com.example.music.dto.YearlyAlbumCountDto;
import com.example.music.entity.AlbumEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumRepository extends ReactiveCrudRepository<AlbumEntity, Long>, AlbumCustomRepository {
    @Query("SELECT release_year, COUNT(*) as album_count " +
            "FROM albums " +
            "GROUP BY release_year " +
            "HAVING release_year IS NOT NULL " +
            "ORDER BY :sortBy :direction " +
            "LIMIT :limit OFFSET :offset")
    Flux<YearlyAlbumCountDto> findYearlyAlbumCounts(int limit, long offset, String sortBy, String direction);

    @Query("SELECT COUNT(DISTINCT release_year) FROM albums WHERE release_year IS NOT NULL")
    Mono<Long> countDistinctYearForAlbums();
}
