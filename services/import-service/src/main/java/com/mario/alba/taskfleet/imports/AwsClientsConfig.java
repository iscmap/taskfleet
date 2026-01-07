package com.mario.alba.taskfleet.imports;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClientsConfig {

    private StaticCredentialsProvider dummyCreds() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy"));
    }

    @Bean
    S3Client s3Client(
            @Value("${aws.localstack.endpoint}") String endpoint,
            @Value("${aws.localstack.region}") String region
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(dummyCreds())
                // LocalStack often needs path-style access
                .forcePathStyle(true)
                .build();
    }

    @Bean
    SqsClient sqsClient(
            @Value("${aws.localstack.endpoint}") String endpoint,
            @Value("${aws.localstack.region}") String region
    ) {
        return SqsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(dummyCreds())
                .build();
    }
}
