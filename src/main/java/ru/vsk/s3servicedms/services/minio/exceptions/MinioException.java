package ru.vsk.s3servicedms.services.minio.exceptions;

public class MinioException extends Exception{
    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
