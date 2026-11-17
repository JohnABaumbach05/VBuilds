package com.vbuilds.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PhotoUploadLimitTest {
    @TempDir Path tempDir;

    private PhotoUploadController controller() {
        PhotoUploadController controller = new PhotoUploadController();
        ReflectionTestUtils.setField(controller, "uploadDirectory", tempDir.toString());
        return controller;
    }

    private MockHttpSession signedIn() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);
        return session;
    }

    private byte[] jpeg(int size) {
        byte[] bytes = new byte[size];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        return bytes;
    }

    @Test void acceptsImageBetweenFiveAndTenMegabytes() throws Exception {
        var file = new MockMultipartFile("file", "large.jpg", "image/jpeg",
                jpeg(6 * 1024 * 1024));
        String url = controller().uploadPhoto(file, signedIn()).get("url");
        assertTrue(url.startsWith("/uploads/builds/"));
        assertTrue(Files.exists(tempDir.resolve(url.substring(url.lastIndexOf('/') + 1))));
    }

    @Test void rejectsImagesLargerThanTenMegabytes() {
        var file = new MockMultipartFile("file", "oversize.jpg", "image/jpeg",
                jpeg(10 * 1024 * 1024 + 1));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller().uploadPhoto(file, signedIn()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("10 MB"));
    }

    @Test void rejectsInvalidImageSignature() {
        var file = new MockMultipartFile("file", "fake.jpg", "image/jpeg",
                new byte[100]);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller().uploadPhoto(file, signedIn()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
