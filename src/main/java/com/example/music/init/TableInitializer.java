package com.example.music.init;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class TableInitializer extends ConnectionFactoryInitializer {

    public TableInitializer(ConnectionFactory connectionFactory) {
        super.setConnectionFactory(connectionFactory);
        super.setDatabasePopulator(
                new ResourceDatabasePopulator(
                        new ClassPathResource("db/migration/V1__create_table.sql"),
                        new ClassPathResource("db/migration/V1__modify_similar_songs.sql")
                )
        );
    }
}
