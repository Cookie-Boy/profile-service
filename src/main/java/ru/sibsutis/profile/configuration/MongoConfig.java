package ru.sibsutis.profile.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${MONGO_HOST:something}")
    private String host;

    @Value("${MONGO_PORT:27017}")
    private String port;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        String connectionString = String.format("mongodb://%s:%s/vet-clinic", host, port);
        return MongoClients.create(connectionString);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "vet-clinic");
    }
}
