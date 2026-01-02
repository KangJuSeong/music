package com.example.music.service;

import com.example.music.dto.MusicSyncContext;
import com.example.music.dto.SongJsonDto;
import com.example.music.entity.ArtistAlbumEntity;
import com.example.music.entity.SimilarSongEntity;
import com.example.music.init.DataInitializer;
import com.example.music.repository.*;
import com.example.music.repository.AlbumRepository;
import com.example.music.repository.song.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MusicSyncService {
    private final DataInitializer dataInitializer;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final SongFeatureRepository songFeatureRepository;
    private final SongMoodRepository songMoodRepository;
    private final ListenContextRepository listenContextRepository;
    private final ArtistAlbumRepository artistAlbumRepository;
    private final SimilarSongRepository similarSongRepository;
    private final File jsonFile;
    private final ConcurrentHashMap<String, Long> albumCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> artistCache = new ConcurrentHashMap<>();
    private final Set<String> artistAlbumCache = ConcurrentHashMap.newKeySet();

    public MusicSyncService(DataInitializer dataInitializer,
                            AlbumRepository albumRepository,
                            SongRepository songRepository,
                            ArtistRepository artistRepository,
                            SongFeatureRepository songFeatureRepository,
                            SongMoodRepository songMoodRepository,
                            ListenContextRepository listenContextRepository,
                            ArtistAlbumRepository artistAlbumRepository,
                            SimilarSongRepository similarSongRepository,
                            @Value("${resource.json.path}") String jsonPath) {
        this.dataInitializer = dataInitializer;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.songFeatureRepository = songFeatureRepository;
        this.songMoodRepository = songMoodRepository;
        this.listenContextRepository = listenContextRepository;
        this.artistAlbumRepository = artistAlbumRepository;
        this.similarSongRepository = similarSongRepository;
        this.jsonFile = new File(jsonPath);
    }

    @Transactional
    public Mono<Void> syncMusicData() {
        log.debug("Start sync music data");
        return dataInitializer.streamJsonToDto(jsonFile)
                // artist, album 저장
                .concatMap(dto -> {
                    MusicSyncContext ctx = new MusicSyncContext(dto);
                    // artist, album 등록 시 연관이 없으므로 병렬 처리
                    return Mono.zip(
                            getOrSaveArtist(ctx).doOnError(th -> log.error("Fail insert artists - {}", ctx)),
                            getOrSaveAlbum(ctx).doOnError(th -> log.error("Fail insert song - {}", ctx))
                    )
                    .thenReturn(ctx);
                })
                .flatMap(ctx ->
                        // song 저장
                        songRepository.save(ctx.getDto().toSongEntity(ctx.getAlbumId(), ctx.getArtistId()))
                        .flatMap(song -> {
                            SongJsonDto dto = ctx.getDto();
                            Long songId = song.getSongId();
                            ctx.setSongId(songId);
                            // song 연관 entity(feature, mood, listenContext, similarSong), artist-album 저장 병렬 처리
                            return Mono.when(
                                    songFeatureRepository.save(dto.toSongFeatureEntity(songId))
                                            .doOnError(th -> log.error("Fail insert feature - {}", ctx)),
                                    songMoodRepository.save(dto.toSongMoodEntity(songId))
                                            .doOnError(th -> log.error("Fail insert mood - {}", ctx)),
                                    listenContextRepository.save(dto.toListenContextEntity(songId))
                                            .doOnError(th -> log.error("Fail insert listen context - {}", ctx)),
                                    saveSimilarSongs(ctx),
                                    saveArtistsAlbums(ctx)
                            );
                        })
                , 60)
                .doFinally(signalType -> {
                    log.debug("Finish sync music data");
                    artistCache.clear();
                    albumCache.clear();
                    artistAlbumCache.clear();
                })
                .then();
    }

    public Mono<MusicSyncContext> getOrSaveArtist(MusicSyncContext ctx) {
        String artistName = ctx.getDto().getArtist();
        if (artistCache.containsKey(artistName)) {
            ctx.setArtistId(artistCache.get(artistName));
            return Mono.just(ctx);
        }
        return artistRepository.save(ctx.getDto().toArtistEntity())
                // 저장된 artist 에 대한 caching
                .map(artist -> {
                    artistCache.put(artistName, artist.getArtistId());
                    ctx.setArtistId(artist.getArtistId());
                    return ctx;
                })
                .doOnError(th -> log.error("Fail insert artist - {}", ctx));
    }

    public Mono<MusicSyncContext> getOrSaveAlbum(MusicSyncContext ctx) {
        String albumName = ctx.getDto().getAlbum();
        if (albumCache.containsKey(albumName)) {
            ctx.setAlbumId(albumCache.get(albumName));
            return Mono.just(ctx);
        }
        return albumRepository.save(ctx.getDto().toAlbumEntity())
                .map(album -> {
                    albumCache.put(albumName, album.getAlbumId());
                    ctx.setAlbumId(album.getAlbumId());
                    return ctx;
                })
                .doOnError(th -> log.error("Fail insert album - {}", ctx));
    }

    public Flux<ArtistAlbumEntity> saveArtistsAlbums(MusicSyncContext ctx) {
        Long albumId = ctx.getAlbumId();
        String albumIdToString = albumId.toString();
        String artistName = ctx.getDto().getArtist();
        List<ArtistAlbumEntity> artistAlbumEntities = Arrays.stream(artistName.split(","))
                .map(String::trim)
                .filter(sa -> artistAlbumCache.add(sa + "|" + albumIdToString))
                .map(sa -> new ArtistAlbumEntity(sa, albumId))
                .toList();
        return artistAlbumRepository.saveAll(artistAlbumEntities)
                .onErrorResume(th -> {
                    if (th instanceof DuplicateKeyException) {
                        log.debug("Duplicate key insert for artist album");
                        return Mono.empty();
                    } else {
                        log.error("Fail insert artist album - {}", artistAlbumEntities);
                        return Mono.error(th);
                    }
                });
    }

   public Flux<SimilarSongEntity> saveSimilarSongs(MusicSyncContext ctx) {
        List<SimilarSongEntity> similarSongEntities = ctx.getDto().getSimilarSongs()
                .stream()
                .filter(similar -> similar.getSimilarSong() != null
                        && similar.getSimilarArtist() != null)
                .map(similar -> similar.toSimilarSongEntity(ctx.getSongId()))
                .toList();
        return similarSongRepository.saveAll(similarSongEntities)
                .doOnError(th -> log.error("Fail insert similar songs - {}", similarSongEntities));
    }
}
