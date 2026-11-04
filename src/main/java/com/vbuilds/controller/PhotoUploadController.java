
package com.vbuilds.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/builds")
public class PhotoUploadController {

    @Value("${vbuilds.upload-dir:uploads/builds}")
    private String uploadDirectory;

    @PostMapping(
            value = "/upload-photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, String> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) throws IOException {


        Integer userId =
                (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in"
            );
        }

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please select a photo"
            );
        }


        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum file size is 5 MB"
            );
        }


        byte[] header = new byte[12];

        try (var input = file.getInputStream()) {
            int count = input.readNBytes(
                    header, 0, header.length
            );

            if (count < 12) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid image"
                );
            }
        }

        boolean jpeg =
                (header[0] & 0xFF) == 0xFF &&
                        (header[1] & 0xFF) == 0xD8 &&
                        (header[2] & 0xFF) == 0xFF;

        boolean png =
                (header[0] & 0xFF) == 0x89 &&
                        header[1] == 0x50 &&
                        header[2] == 0x4E &&
                        header[3] == 0x47 &&
                        header[4] == 0x0D &&
                        header[5] == 0x0A &&
                        header[6] == 0x1A &&
                        header[7] == 0x0A;

        boolean webp =
                new String(header, 0, 4,
                        java.nio.charset.StandardCharsets.US_ASCII)
                        .equals("RIFF") &&
                        new String(header, 8, 4,
                                java.nio.charset.StandardCharsets.US_ASCII)
                                .equals("WEBP");

        if (!jpeg && !png && !webp) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only JPG, PNG and WebP are supported"
            );
        }
        String extension =
                jpeg ? ".jpg" : png ? ".png" : ".webp";

        String filename = UUID.randomUUID() + extension;

        Path directory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(directory);
        Path destination = directory.resolve(filename);

        try (var input = file.getInputStream()) {
            Files.copy(input, destination);
        }

        String imageUrl = "/uploads/builds/" + filename;

        return Map.of("url", imageUrl);
    }
}
