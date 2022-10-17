package ru.vsk.s3servicedms.services.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("spring.minio")
@Data
public class MinioConfigurationProperties {
    private String url = "http://127.0.0.1:9000";

    /**
     * Access key (login) on Minio instance
     */
    private String accessKey = "ROOTUSER";

    /**
     * Secret key (password) on Minio instance
     */
    private String secretKey = "CHANGEME123";

    /**
     * If the scheme is not provided in {@code url} property, define if the connection is done via HTTP or HTTPS.
     */
    private boolean secure = false;

    /**
     * Bucket name for the application. The bucket must already exists on Minio.
     */
    private String bucket = "test";

    /**
     * Metric configuration prefix which are registered on Actuator.
     */
    private String metricName = "minio.storage";

    /**
     * Define the connection timeout for the Minio Client.
     */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * Define the writing timeout for the Minio Client.
     */
    private Duration writeTimeout = Duration.ofSeconds(60);

    /**
     * Define the read timeout for the Minio Client.
     */
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * Check if the bucket exists on Minio instance.
     * Settings this false will disable the check during the application context initialization.
     * This property should be used for debug purpose only, because operations on Minio will not work during runtime.
     */
    private boolean checkBucket = true;

    /**
     * Will create the bucket if it do not exists on the Minio instance.
     */
    private boolean createBucket = true;
}
