package com.vbuilds.controller;

import com.vbuilds.model.Photo;
import com.vbuilds.service.PhotoService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")

public class PhotoController {

    private PhotoService photoService;

    public PhotoController() throws SQLException {
        photoService = new PhotoService();
    }

    @PostMapping
    public void addPhoto(@RequestBody Photo photo) throws SQLException {
        photoService.addPhoto(photo);
    }

    @DeleteMapping("/{id}")
    public void deletePhoto(@PathVariable int id) throws SQLException {
        photoService.deletePhoto(id);
    }

    @PutMapping("/{id}")
    public void updatePhoto(@PathVariable int id, @RequestBody Photo photo) throws SQLException {
        photo.setPhotoId(id);
        photoService.updatePhoto(photo);
    }

    @GetMapping("/{id}")
    public Photo getPhotoById(@PathVariable int id) throws SQLException {
        return photoService.getPhotoById(id);
    }


    @GetMapping("/build/buildId")
    public List<Photo> getPhotosByBuild(@PathVariable int buildId) throws SQLException {
        return photoService.getPhotosByBuild(buildId);
    }



}
