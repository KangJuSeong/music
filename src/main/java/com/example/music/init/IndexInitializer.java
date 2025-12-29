package com.example.music.init;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IndexInitializer {
    private final DatabaseClient client;
    public IndexInitializer(DatabaseClient client) {
        this.client = client;
    }

    @PostConstruct
    public void indexInit() {
        IndexInfo[] indexInfos = new IndexInfo[] {
                new IndexInfo("idx_release_date", "songs", "release_date"),
                new IndexInfo("idx_artist", "songs", "artist")
        };

        for (IndexInfo indexInfo : indexInfos) {
            createIndexIfNotExists(indexInfo);
        }
    }

    private void createIndexIfNotExists(IndexInfo indexInfo) {
        String isExistIndex=
                """
                SELECT COUNT(*) AS count
                FROM INFORMATION_SCHEMA.STATISTICS
                WHERE TABLE_SCHEMA = 'music'
                    AND TABLE_NAME = :table
                    AND INDEX_NAME = :name
                """;
        String createIndex =
                """
                CREATE INDEX %s
                ON %s(%s)
                """
                .formatted(indexInfo.name, indexInfo.table, indexInfo.columnName);
        client.sql(isExistIndex)
                .bind("table", indexInfo.table)
                .bind("name", indexInfo.name)
                .map(result -> result.get("count", Integer.class))
                .first()
                .flatMap(count -> {
                    if (count == 0) {
                        return client.sql(createIndex).then();
                    } else {
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private record IndexInfo(String name, String table, String columnName) {}
}
