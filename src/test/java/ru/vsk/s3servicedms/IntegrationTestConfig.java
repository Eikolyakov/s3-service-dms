package ru.vsk.s3servicedms;

import io.minio.MinioClient;
import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.vsk.s3servicedms.services.io.FileUploadService;
import ru.vsk.s3servicedms.services.minio.MinioService;
import ru.vsk.s3servicedms.services.minio.config.MinioConfiguration;
import ru.vsk.s3servicedms.services.minio.config.MinioConfigurationProperties;

@Configuration
@Import(MinioConfiguration.class)
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class, // Create required server beans
        GrpcServerFactoryAutoConfiguration.class, // Select server implementation
        GrpcClientAutoConfiguration.class})// Support @GrpcClient annotation
public class IntegrationTestConfig {
    @Autowired
    MinioService minioService;

    @Bean
    FileUploadService fileUploadService() {
        return new FileUploadService(minioService);
    }
}
