package com.vbuilds.controller;

import com.vbuilds.model.Build;
import com.vbuilds.model.Photo;
import com.vbuilds.service.BuildService;
import com.vbuilds.service.PhotoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final BuildService buildService;

    public PhotoController() throws SQLException {
        this(new PhotoService(), new BuildService());
    }

    PhotoController(PhotoService photoService, BuildService buildService) {
        this.photoService = photoService;
        this.buildService = buildService;
    }

    private void requireOwnedBuild(int buildId, HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        Build build = buildService.getBuildForEditing(buildId);
        if (build == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found");
        }
        if (build.getOwnerID() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your build");
        }
    }

    private Photo requireOwnedPhoto(int id, HttpSession session) throws SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        Photo existing = photoService.getPhotoById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found");
        }
        requireOwnedBuild(existing.getBuildId(), session);
        return existing;
    }

    private void validatePhotoPath(String path) {
        if (path == null || !path.startsWith("/uploads/builds/") ||
                path.contains("..") || path.contains("\\") || path.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid photo path");
        }
    }

    @PostMapping
    public void addPhoto(@RequestBody Photo photo, HttpSession session) throws SQLException {
        if (photo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo required");
        }
        requireOwnedBuild(photo.getBuildId(), session);
        validatePhotoPath(photo.getFilePath());
        photoService.addPhoto(photo);
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable int id, HttpSession session) throws SQLException {
        requireOwnedPhoto(id, session);
        photoService.deletePhoto(id);
    }

    @PutMapping("/{id}")
    public void updatePhoto(@PathVariable int id, @RequestBody Photo photo,
                            HttpSession session) throws SQLException {
        Photo existing = requireOwnedPhoto(id, session);
        if (photo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo required");
        }
        validatePhotoPath(photo.getFilePath());
        photo.setPhotoId(id);
        photo.setBuildId(existing.getBuildId());
        photoService.updatePhoto(photo);
    }

    @GetMapping("/{id}")
    public Photo getPhotoById(@PathVariable int id) throws SQLException {
        return photoService.getPhotoById(id);
    }

    @GetMapping("/build/{buildId}")
    public List<Photo> getPhotosByBuild(@PathVariable int buildId) throws SQLException {
        return photoService.getPhotosByBuild(buildId);
    }
}
