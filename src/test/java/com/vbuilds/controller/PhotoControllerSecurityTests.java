package com.vbuilds.controller;

import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.service.BuildService;
import com.vbuilds.service.PhotoService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoControllerSecurityTests {
    private PhotoService photos;
    private BuildService builds;
    private PhotoController controller;
    private HttpSession session;

    @BeforeEach
    void setup() {
        photos = mock(PhotoService.class);
        builds = mock(BuildService.class);
        session = mock(HttpSession.class);
        controller = new PhotoController(photos, builds);
    }

    @Test
    void anonymousUserCannotCreatePhoto() throws Exception {
        Photo photo = new Photo();
        photo.setBuildId(12);
        photo.setFilePath("/uploads/builds/test.jpg");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.addPhoto(photo, session));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verify(photos, never()).addPhoto(any());
    }

    @Test
    void otherUserCannotCreatePhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(8);
        Build build = new Build();
        build.setOwnerID(7);
        when(builds.getBuildForEditing(12)).thenReturn(build);

        Photo photo = new Photo();
        photo.setBuildId(12);
        photo.setFilePath("/uploads/builds/test.jpg");

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.addPhoto(photo, session));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(photos, never()).addPhoto(any());
    }

    @Test
    void ownerCanCreatePhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(7);
        Build build = new Build();
        build.setOwnerID(7);
        when(builds.getBuildForEditing(12)).thenReturn(build);

        Photo photo = new Photo();
        photo.setBuildId(12);
        photo.setFilePath("/uploads/builds/test.jpg");
        controller.addPhoto(photo, session);
        verify(photos).addPhoto(photo);
    }

    @Test
    void otherUserCannotDeletePhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(8);
        Photo photo = new Photo();
        photo.setBuildId(12);
        when(photos.getPhotoById(99)).thenReturn(photo);
        Build build = new Build();
        build.setOwnerID(7);
        when(builds.getBuildForEditing(12)).thenReturn(build);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.deletePhoto(99, session));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(photos, never()).deletePhoto(anyInt());
    }

    @Test
    void otherUserCannotUpdatePhoto() throws Exception {
        when(session.getAttribute("userId")).thenReturn(8);
        Photo existing = new Photo();
        existing.setBuildId(12);
        when(photos.getPhotoById(99)).thenReturn(existing);
        Build build = new Build();
        build.setOwnerID(7);
        when(builds.getBuildForEditing(12)).thenReturn(build);

        Photo replacement = new Photo();
        replacement.setFilePath("/uploads/builds/replacement.jpg");
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.updatePhoto(99, replacement, session));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(photos, never()).updatePhoto(any());
    }

    @Test
    void ownerCannotSubmitTraversalPath() throws Exception {
        when(session.getAttribute("userId")).thenReturn(7);
        Build build = new Build();
        build.setOwnerID(7);
        when(builds.getBuildForEditing(12)).thenReturn(build);

        Photo photo = new Photo();
        photo.setBuildId(12);
        photo.setFilePath("/uploads/builds/../profiles/other.jpg");
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.addPhoto(photo, session));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(photos, never()).addPhoto(any());
    }
}
