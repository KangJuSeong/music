package com.example.music.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class IndexInitializer implements ApplicationRunner {
    private final DatabaseClient client;
    public IndexInitializer(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        IndexInfo[] indexInfos = new IndexInfo[] {
                new IndexInfo("idx_release_date", "albums", "release_date"),
                new IndexInfo("idx_artist", "artists", "artist_name")
        };

        for (IndexInfo indexInfo : indexInfos) {
            createIndexIfNotExists(indexInfo);
        }
    }

    private void createIndexIfNotExists(IndexInfo indexInfo) {
        String isExistIndexQuery=
                """
                SELECT COUNT(*) AS count
                FROM INFORMATION_SCHEMA.STATISTICS
                WHERE TABLE_SCHEMA = 'music'
                    AND TABLE_NAME = :table
                    AND INDEX_NAME = :name
                """;
        String createIndexQuery =
                """
                CREATE INDEX %s
                ON %s(%s)
                """
                .formatted(indexInfo.name, indexInfo.table, indexInfo.columnName);
        client.sql(isExistIndexQuery)
                .bind("table", indexInfo.table)
                .bind("name", indexInfo.name)
                .map(result -> result.get("count", Integer.class))
                .first()
                .flatMap(count -> {
                    if (count == 0) {
                        return client.sql(createIndexQuery).then();
                    } else {
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private record IndexInfo(String name, String table, String columnName) {}
}
