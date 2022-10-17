package ru.vsk.s3servicedms.services.minio.exceptions;

public class MinioFetchException extends RuntimeException {
    public MinioFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
