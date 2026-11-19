package com.vbuilds.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageStorageTest {
    @Test
    void acceptsExistingLocalBuildPhotos() {
        assertTrue(ImageStorage.validBuildUrl("/uploads/builds/photo.jpg"));
    }

    @Test
    void rejectsTraversalAndExternalUrls() {
        assertFalse(ImageStorage.validBuildUrl("/uploads/builds/../profiles/photo.jpg"));
        assertFalse(ImageStorage.validBuildUrl("/uploads/builds/\\\\secret.jpg"));
        assertFalse(ImageStorage.validBuildUrl("https://example.com/photo.jpg"));
        assertFalse(ImageStorage.validBuildUrl(null));
    }
}
