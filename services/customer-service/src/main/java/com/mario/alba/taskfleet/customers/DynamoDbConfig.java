package com.mario.alba.taskfleet.customers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    @Bean
    DynamoDbClient dynamoDbClient(
            @Value("${aws.dynamodb.endpoint}") String endpoint,
            @Value("${aws.dynamodb.region}") String region
    ) {
        // DynamoDB Local accepts any credentials
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy"))
                )
                .build();
    }
}
