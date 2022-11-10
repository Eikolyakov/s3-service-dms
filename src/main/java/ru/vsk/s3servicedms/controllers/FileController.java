package ru.vsk.s3servicedms.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsk.s3servicedms.services.minio.MinioService;
import ru.vsk.s3servicedms.services.minio.exceptions.MinioException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;

@RequiredArgsConstructor
@RestController
@RequestMapping("/files")
public class FileController {
    private final MinioService minioService;


    @PostMapping()
    public void uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucket") String bucket) {
        Path path = Path.of(file.getOriginalFilename());
        try {
            minioService.upload(path, file.getInputStream(), file.getContentType(), bucket);
        } catch (MinioException e) {
            throw new IllegalStateException("The file cannot be upload on the internal storage. Please retry later", e);
        } catch (IOException e) {
            throw new IllegalStateException("The file cannot be read", e);
        }
    }


    @GetMapping("/{object}")
    public void getFile(@PathVariable("object") String object, HttpServletResponse response) throws MinioException, IOException {
        InputStream inputStream = minioService.get(Path.of(object));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=" + object);
        response.setContentType(URLConnection.guessContentTypeFromName(object));

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }
}
