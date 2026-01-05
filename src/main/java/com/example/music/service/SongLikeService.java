package com.example.music.service;

import com.example.music.dto.SongLikeDto;
import com.example.music.entity.SongLikeEntity;
import com.example.music.repository.song.SongRepository;
import com.example.music.repository.songLike.SongLikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SongLikeService {
    private final SongLikeRepository songLikeRepository;
    private final SongRepository songRepository;

    public SongLikeService(SongLikeRepository songLikeRepository, SongRepository songRepository) {
        this.songLikeRepository = songLikeRepository;
        this.songRepository = songRepository;
    }

    public Mono<Void> insertSongLike(Long songId) {
        return songRepository.findById(songId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not exists song")))
                .flatMap(s -> {
                    SongLikeEntity likeEntity = new SongLikeEntity(s.getSongId());
                    return songLikeRepository.save(likeEntity).then();
                });
    }

    public Flux<SongLikeDto> getSongLikeIncreaseTop(Integer top) {
        return songLikeRepository.findTopLikeCount(top);
    }
}
