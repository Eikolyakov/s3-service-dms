package ru.vsk.s3servicedms.services.io;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.vsk.s3servicedms.FileServiceGrpc;
import ru.vsk.s3servicedms.FileUploadRequest;
import ru.vsk.s3servicedms.FileUploadResponse;
import ru.vsk.s3servicedms.Status;
import ru.vsk.s3servicedms.services.minio.MinioService;
import ru.vsk.s3servicedms.services.minio.exceptions.MinioException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


@RequiredArgsConstructor
@GrpcService
public class FileUploadService extends FileServiceGrpc.FileServiceImplBase {
    private final MinioService minioService;

    private static final Path SERVER_BASE_PATH = Paths.get("src/test/resources/output");

    @Override
    public StreamObserver<FileUploadRequest> upload(StreamObserver<FileUploadResponse> responseObserver) {
        return new StreamObserver<FileUploadRequest>() {
            ByteArrayOutputStream writer;
            Status status = Status.IN_PROGRESS;
            Path path;

            @Override
            public void onNext(FileUploadRequest fileUploadRequest) {
                try{
                    if(fileUploadRequest.hasMetadata()){
                        path = getFilePath(fileUploadRequest);
                    }else{
                        writeFile(writer, fileUploadRequest.getFile().getContent());
                    }
                }catch (IOException e){
                    this.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                status = Status.FAILED;
                this.onCompleted();
            }

            @Override
            public void onCompleted() {
                InputStream is = new ByteArrayInputStream(writer.toByteArray());
                try {
                    minioService.upload(path, is);
                } catch (MinioException e) {
                    throw new RuntimeException(e);
                }
                closeFile(writer);
                status = Status.IN_PROGRESS.equals(status) ? Status.SUCCESS : status;
                FileUploadResponse response = FileUploadResponse.newBuilder()
                        .setStatus(status)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    private Path getFilePath(FileUploadRequest request) throws IOException {
        var fileName = request.getMetadata().getName() + "." + request.getMetadata().getType();
        return Path.of(fileName);
    }

    private void writeFile(OutputStream writer, ByteString content) throws IOException {
        writer.write(content.toByteArray());
        writer.flush();
    }

    private void closeFile(OutputStream writer){
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}