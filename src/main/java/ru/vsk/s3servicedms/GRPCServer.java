package ru.vsk.s3servicedms;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vsk.s3servicedms.services.io.FileUploadService;
import ru.vsk.s3servicedms.services.minio.MinioService;

@Service
public class GRPCServer {
    @Autowired
    MinioService minioService;

    private Server server;

    public void start(){
        try{
            // build gRPC server
            this.server = ServerBuilder.forPort(6565)
                    .addService(new FileUploadService(minioService))
                    .build();

            // start
            server.start();

            // shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("gRPC server is shutting down!");
                server.shutdown();
            }));

            server.awaitTermination();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        this.server.shutdownNow();
    }

}